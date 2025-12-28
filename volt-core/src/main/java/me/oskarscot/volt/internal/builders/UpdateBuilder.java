package me.oskarscot.volt.internal.builders;

import me.oskarscot.volt.entity.EntityDefinition;
import me.oskarscot.volt.entity.FieldDefinition;
import me.oskarscot.volt.entity.PrimaryKey;
import me.oskarscot.volt.internal.registry.ConverterRegistry;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class UpdateBuilder<T> implements EntitySqlBuilder<T> {

  private final EntityDefinition<T> definition;
  private final T entity;
  private final ConverterRegistry converterRegistry;

  public UpdateBuilder(
      EntityDefinition<T> definition, T entity, ConverterRegistry converterRegistry) {
    this.definition = definition;
    this.entity = entity;
    this.converterRegistry = converterRegistry;
  }

  @Override
  public String toSql() {
    String sql = "UPDATE %s SET %s WHERE %s = ?";
    String tableName = definition.getTableName();

    String setClause =
        definition.getFields().stream()
            .map(field -> field.getColumnName() + " = ?")
            .collect(Collectors.joining(", "));

    String pkColumn = definition.getPrimaryKey().getColumnName();

    return String.format(sql, tableName, setClause, pkColumn);
  }

  @Override
  public void bindValues(PreparedStatement stmt) throws SQLException, IllegalAccessException {
    int index = 1;

    for (FieldDefinition field : definition.getFields()) {
      field.getField().setAccessible(true);
      Object value = field.getField().get(entity);
      converterRegistry.write(stmt, index++, value, field.getField().getType());
    }

    PrimaryKey pk = definition.getPrimaryKey();
    pk.getField().setAccessible(true);
    Object pkValue = pk.getField().get(entity);
    converterRegistry.write(stmt, index, pkValue, pk.getField().getType());
  }
}
