package me.oskarscot.volt.internal.builders;

import me.oskarscot.volt.entity.EntityDefinition;
import me.oskarscot.volt.entity.FieldDefinition;
import me.oskarscot.volt.entity.PrimaryKey;
import me.oskarscot.volt.entity.PrimaryKeyType;
import me.oskarscot.volt.internal.registry.ConverterRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class InsertBuilder<T> implements EntitySqlBuilder<T> {

  private final EntityDefinition<T> definition;
  private final T entity;
  private final ConverterRegistry converterRegistry;

  public InsertBuilder(
      EntityDefinition<T> definition, T entity, ConverterRegistry converterRegistry) {
    this.definition = definition;
    this.entity = entity;
    this.converterRegistry = converterRegistry;
  }

  @Override
  public String toSql() {
    String sql = "INSERT INTO %s (%s) VALUES (%s)";
    String tableName = definition.getTableName();

    List<String> columns = new ArrayList<>();

    PrimaryKey pk = definition.getPrimaryKey();
    if (shouldIncludePrimaryKey(pk)) {
      columns.add(pk.getColumnName());
    }

    for (FieldDefinition field : definition.getFields()) {
      columns.add(field.getColumnName());
    }

    String fieldList = String.join(", ", columns);
    String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(", "));

    return String.format(sql, tableName, fieldList, placeholders);
  }

  @Override
  public void bindValues(PreparedStatement stmt) throws SQLException, IllegalAccessException {
    int index = 1;
    PrimaryKey pk = definition.getPrimaryKey();
    if (shouldIncludePrimaryKey(pk)) {
      Object pkValue;
      if (pk.isGenerated() && pk.getPrimaryKeyType() == PrimaryKeyType.UUID) {
        pkValue = UUID.randomUUID();
        pk.getField().setAccessible(true);
        pk.getField().set(entity, pkValue);
      } else {
        pk.getField().setAccessible(true);
        pkValue = pk.getField().get(entity);
      }
      converterRegistry.write(stmt, index++, pkValue, pk.getField().getType());
    }

    for (FieldDefinition field : definition.getFields()) {
      field.getField().setAccessible(true);
      Object value = field.getField().get(entity);
      converterRegistry.write(stmt, index++, value, field.getField().getType());
    }
  }

  private boolean shouldIncludePrimaryKey(PrimaryKey pk) {
    if (!pk.isGenerated()) {
      return true;
    }
    return pk.getPrimaryKeyType() == PrimaryKeyType.UUID;
  }
}
