package me.oskarscot.volt.internal.builders;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import me.oskarscot.volt.entity.EntityDefinition;
import me.oskarscot.volt.entity.FieldDefinition;
import me.oskarscot.volt.entity.PrimaryKey;
import me.oskarscot.volt.entity.PrimaryKeyType;
import me.oskarscot.volt.internal.registry.ConverterRegistry;

public class UpsertBuilder<T> implements EntitySqlBuilder<T> {

  private final EntityDefinition<T> definition;
  private final T entity;
  private final ConverterRegistry converterRegistry;

  public UpsertBuilder(
      EntityDefinition<T> definition, T entity, ConverterRegistry converterRegistry) {
    this.definition = definition;
    this.entity = entity;
    this.converterRegistry = converterRegistry;
  }

  @Override
  public String toSql() {
    String tableName = definition.getTableName();
    PrimaryKey pk = definition.getPrimaryKey();

    List<String> columns = new ArrayList<>();
    columns.add(pk.getColumnName());
    for (FieldDefinition field : definition.getFields()) {
      columns.add(field.getColumnName());
    }

    String columnList = String.join(", ", columns);
    String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(", "));

    String updateSet =
        definition.getFields().stream()
            .map(field -> field.getColumnName() + " = EXCLUDED." + field.getColumnName())
            .collect(Collectors.joining(", "));

    return String.format(
        "INSERT INTO %s (%s) VALUES (%s) ON CONFLICT (%s) DO UPDATE SET %s",
        tableName, columnList, placeholders, pk.getColumnName(), updateSet);
  }

  @Override
  public void bindValues(PreparedStatement stmt) throws SQLException, IllegalAccessException {
    int index = 1;

    PrimaryKey pk = definition.getPrimaryKey();
    pk.getField().setAccessible(true);
    Object pkValue = pk.getField().get(entity);

    if (pkValue == null && pk.isGenerated() && pk.getPrimaryKeyType() == PrimaryKeyType.UUID) {
      pkValue = UUID.randomUUID();
      pk.getField().set(entity, pkValue);
    }

    converterRegistry.write(stmt, index++, pkValue, pk.getField().getType());

    for (FieldDefinition field : definition.getFields()) {
      field.getField().setAccessible(true);
      Object value = field.getField().get(entity);
      converterRegistry.write(stmt, index++, value, field.getField().getType());
    }
  }
}
