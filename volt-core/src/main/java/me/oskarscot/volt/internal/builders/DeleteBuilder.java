package me.oskarscot.volt.internal.builders;

import me.oskarscot.volt.entity.EntityDefinition;
import me.oskarscot.volt.query.Query;

public class DeleteBuilder implements SqlBuilder {

  private final EntityDefinition<?> definition;

  public DeleteBuilder(EntityDefinition<?> definition) {
    this.definition = definition;
  }

  @Override
  public String toSql() {
    return String.format(
        "DELETE FROM %s WHERE %s = ?",
        definition.getTableName(), definition.getPrimaryKey().getColumnName());
  }

  public String toSqlWithQuery(Query query) {
    return String.format(
        "DELETE FROM %s WHERE %s", definition.getTableName(), query.toWhereClause());
  }
}
