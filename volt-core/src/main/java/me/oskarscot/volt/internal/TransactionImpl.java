package me.oskarscot.volt.internal;

import me.oskarscot.volt.Result;
import me.oskarscot.volt.Transaction;
import me.oskarscot.volt.entity.EntityDefinition;
import me.oskarscot.volt.entity.FieldDefinition;
import me.oskarscot.volt.entity.PrimaryKey;
import me.oskarscot.volt.entity.PrimaryKeyType;
import me.oskarscot.volt.exception.VoltError;
import me.oskarscot.volt.internal.builders.DeleteBuilder;
import me.oskarscot.volt.internal.builders.InsertBuilder;
import me.oskarscot.volt.internal.builders.SelectBuilder;
import me.oskarscot.volt.internal.builders.UpdateBuilder;
import me.oskarscot.volt.internal.builders.UpsertBuilder;
import me.oskarscot.volt.internal.registry.ConverterRegistry;
import me.oskarscot.volt.internal.registry.EntityRegistry;
import me.oskarscot.volt.query.Condition;
import me.oskarscot.volt.query.Query;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TransactionImpl implements Transaction {

    private final EntityRegistry entityRegistry;
    private final ConverterRegistry converterRegistry;
    private final ConnectionManager connectionManager;
    private final Connection connection;

    public TransactionImpl(EntityRegistry entityRegistry, ConverterRegistry converterRegistry,
                           ConnectionManager connectionManager, Connection connection) {
        this.entityRegistry = entityRegistry;
        this.converterRegistry = converterRegistry;
        this.connectionManager = connectionManager;
        this.connection = connection;
    }

  @Override
  public <T> Result<T, VoltError> save(T entity) {
    EntityDefinition<T> definition = (EntityDefinition<T>) entityRegistry.get(entity.getClass());

    if (definition == null) {
      return Result.failure(new VoltError("Entity " + entity.getClass().getName() + " is not registered"));
    }

    PrimaryKey pk = definition.getPrimaryKey();
    pk.getField().setAccessible(true);

    Object pkValue;
    try {
      pkValue = pk.getField().get(entity);
    } catch (IllegalAccessException e) {
      return Result.failure(new VoltError("Cannot access primary key: " + e.getMessage()));
    }

    if (pkValue == null && pk.isGenerated() && pk.getPrimaryKeyType() == PrimaryKeyType.NUMBER) {
      return insert(entity, definition);
    }

    return upsert(entity, definition);
  }

  private <T> Result<T, VoltError> upsert(T entity, EntityDefinition<T> definition) {
    UpsertBuilder<T> builder = new UpsertBuilder<>(definition, entity, converterRegistry);
    String sql = builder.toSql();

    try (PreparedStatement stmt = connection.prepareStatement(sql)) {
      builder.bindValues(stmt);
      stmt.executeUpdate();
      return Result.okay(entity);
    } catch (SQLException | IllegalAccessException e) {
      return Result.failure(new VoltError("Upsert failed: " + e.getMessage()));
    }
  }

    @Override
    public <T> Result<T, VoltError> findById(Class<T> type, Object id) {
        EntityDefinition<T> definition = (EntityDefinition<T>) entityRegistry.get(type);

        if (definition == null) {
            return Result.failure(new VoltError("Entity " + type.getName() + " is not registered"));
        }

        SelectBuilder builder = new SelectBuilder(definition);
        String sql = builder.toSqlById();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            PrimaryKey pk = definition.getPrimaryKey();
            converterRegistry.write(stmt, 1, id, pk.getField().getType());

            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                T entity = mapRow(rs, definition);
                return Result.okay(entity);
            } else {
                return Result.failure(new VoltError("Entity not found with id: " + id));
            }
        } catch (SQLException | ReflectiveOperationException e) {
            return Result.failure(new VoltError("Find by id failed: " + e.getMessage()));
        }
    }

    @Override
    public <T> Result<Optional<T>, VoltError> findFirstBy(Class<T> type, String field, Object value) {
        Query query = Query.where(field).eq(value);
        return findFirstBy(type, query);
    }

    @Override
    public <T> Result<T, VoltError> findOneBy(Class<T> type, String field, Object value) {
        Query query = Query.where(field).eq(value);
        return findOneBy(type, query);
    }

    @Override
    public <T> Result<List<T>, VoltError> findAllBy(Class<T> type, String field, Object value) {
        Query query = Query.where(field).eq(value);
        return findAllBy(type, query);
    }

    @Override
    public <T> Result<List<T>, VoltError> findAll(Class<T> type) {
        EntityDefinition<T> definition = (EntityDefinition<T>) entityRegistry.get(type);

        if (definition == null) {
            return Result.failure(new VoltError("Entity " + type.getName() + " is not registered"));
        }

        SelectBuilder builder = new SelectBuilder(definition);
        String sql = builder.toSql();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapRow(rs, definition));
            }

            return Result.okay(results);
        } catch (SQLException | ReflectiveOperationException e) {
            return Result.failure(new VoltError("Find all failed: " + e.getMessage()));
        }
    }

    @Override
    public <T> Result<Optional<T>, VoltError> findFirstBy(Class<T> type, Query query) {
        EntityDefinition<T> definition = (EntityDefinition<T>) entityRegistry.get(type);

        if (definition == null) {
            return Result.failure(new VoltError("Entity " + type.getName() + " is not registered"));
        }

        SelectBuilder builder = new SelectBuilder(definition);
        String sql = builder.toSqlWithQuery(query) + " LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            bindQueryValues(stmt, query);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                T entity = mapRow(rs, definition);
                return Result.okay(Optional.of(entity));
            } else {
                return Result.okay(Optional.empty());
            }
        } catch (SQLException | ReflectiveOperationException e) {
            return Result.failure(new VoltError("Find first failed: " + e.getMessage()));
        }
    }

    @Override
    public <T> Result<T, VoltError> findOneBy(Class<T> type, Query query) {
        EntityDefinition<T> definition = (EntityDefinition<T>) entityRegistry.get(type);

        if (definition == null) {
            return Result.failure(new VoltError("Entity " + type.getName() + " is not registered"));
        }

        SelectBuilder builder = new SelectBuilder(definition);
        String sql = builder.toSqlWithQuery(query);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            bindQueryValues(stmt, query);
            ResultSet rs = stmt.executeQuery();

            if (!rs.next()) {
                return Result.failure(new VoltError("Entity not found"));
            }

            T entity = mapRow(rs, definition);

            if (rs.next()) {
                return Result.failure(new VoltError("Expected one result but found multiple"));
            }

            return Result.okay(entity);
        } catch (SQLException | ReflectiveOperationException e) {
            return Result.failure(new VoltError("Find one failed: " + e.getMessage()));
        }
    }

    @Override
    public <T> Result<List<T>, VoltError> findAllBy(Class<T> type, Query query) {
        EntityDefinition<T> definition = (EntityDefinition<T>) entityRegistry.get(type);

        if (definition == null) {
            return Result.failure(new VoltError("Entity " + type.getName() + " is not registered"));
        }

        SelectBuilder builder = new SelectBuilder(definition);
        String sql = builder.toSqlWithQuery(query);

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            bindQueryValues(stmt, query);
            ResultSet rs = stmt.executeQuery();

            List<T> results = new ArrayList<>();
            while (rs.next()) {
                results.add(mapRow(rs, definition));
            }

            return Result.okay(results);
        } catch (SQLException | ReflectiveOperationException e) {
            return Result.failure(new VoltError("Find all failed: " + e.getMessage()));
        }
    }

    @Override
    public <T> Result<Void, VoltError> delete(T entity) {
        EntityDefinition<T> definition = (EntityDefinition<T>) entityRegistry.get(entity.getClass());

        if (definition == null) {
            return Result.failure(new VoltError("Entity " + entity.getClass().getName() + " is not registered"));
        }

        PrimaryKey pk = definition.getPrimaryKey();
        pk.getField().setAccessible(true);

        Object pkValue;
        try {
            pkValue = pk.getField().get(entity);
        } catch (IllegalAccessException e) {
            return Result.failure(new VoltError("Cannot access primary key: " + e.getMessage()));
        }

        if (pkValue == null) {
            return Result.failure(new VoltError("Cannot delete entity with null primary key"));
        }

        return deleteById((Class<T>) entity.getClass(), pkValue);
    }

    @Override
    public <T> Result<Void, VoltError> deleteById(Class<T> type, Object id) {
        EntityDefinition<T> definition = (EntityDefinition<T>) entityRegistry.get(type);

        if (definition == null) {
            return Result.failure(new VoltError("Entity " + type.getName() + " is not registered"));
        }

        DeleteBuilder builder = new DeleteBuilder(definition);
        String sql = builder.toSql();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            PrimaryKey pk = definition.getPrimaryKey();
            converterRegistry.write(stmt, 1, id, pk.getField().getType());

            int affected = stmt.executeUpdate();

            if (affected == 0) {
                return Result.failure(new VoltError("Entity not found with id: " + id));
            }

            return Result.okay(null);
        } catch (SQLException e) {
            return Result.failure(new VoltError("Delete failed: " + e.getMessage()));
        }
    }

    @Override
    public Result<Void, VoltError> commit() {
        try {
            connection.commit();
            connection.setAutoCommit(true);
            return Result.okay(null);
        } catch (SQLException e) {
            return Result.failure(new VoltError("Commit failed: " + e.getMessage()));
        }
    }

    @Override
    public Result<Void, VoltError> rollback() {
        try {
            connection.rollback();
            connection.setAutoCommit(true);
            return Result.okay(null);
        } catch (SQLException e) {
            return Result.failure(new VoltError("Rollback failed: " + e.getMessage()));
        }
    }

    @Override
    public void close() {
        connectionManager.release(connection);
    }

    private <T> Result<T, VoltError> insert(T entity, EntityDefinition<T> definition) {
        InsertBuilder<T> builder = new InsertBuilder<>(definition, entity, converterRegistry);
        String sql = builder.toSql();

        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            builder.bindValues(stmt);
            stmt.executeUpdate();

            if (definition.getPrimaryKey().isGenerated() &&
                    definition.getPrimaryKey().getPrimaryKeyType() == PrimaryKeyType.NUMBER) {
                ResultSet keys = stmt.getGeneratedKeys();
                if (keys.next()) {
                    Object generatedId = keys.getLong(1);
                    definition.getPrimaryKey().getField().set(entity, generatedId);
                }
            }

            return Result.okay(entity);
        } catch (SQLException | IllegalAccessException e) {
            return Result.failure(new VoltError("Insert failed: " + e.getMessage()));
        }
    }

    private <T> Result<T, VoltError> update(T entity, EntityDefinition<T> definition) {
        UpdateBuilder<T> builder = new UpdateBuilder<>(definition, entity, converterRegistry);
        String sql = builder.toSql();

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            builder.bindValues(stmt);

            int affected = stmt.executeUpdate();

            if (affected == 0) {
                return Result.failure(new VoltError("Entity not found - update affected 0 rows"));
            }

            return Result.okay(entity);
        } catch (SQLException | IllegalAccessException e) {
            return Result.failure(new VoltError("Update failed: " + e.getMessage()));
        }
    }

    private <T> T mapRow(ResultSet rs, EntityDefinition<T> definition)
            throws SQLException, ReflectiveOperationException {
        T entity = definition.getClazz().getDeclaredConstructor().newInstance();

        PrimaryKey pk = definition.getPrimaryKey();
        Object pkValue = converterRegistry.read(rs, pk.getColumnName(), pk.getField().getType());
        pk.getField().setAccessible(true);
        pk.getField().set(entity, pkValue);

        for (FieldDefinition field : definition.getFields()) {
            Object value = converterRegistry.read(rs, field.getColumnName(), field.getField().getType());
            field.getField().setAccessible(true);
            field.getField().set(entity, value);
        }

        return entity;
    }

    private void bindQueryValues(PreparedStatement stmt, Query query) throws SQLException {
        int index = 1;
        for (Condition condition : query.getConditions()) {
            for (Object value : condition.getValues()) {
                converterRegistry.write(stmt, index++, value, value.getClass());
            }
        }
    }
}
