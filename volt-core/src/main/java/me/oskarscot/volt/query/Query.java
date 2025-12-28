package me.oskarscot.volt.query;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Query {

  private final List<Condition> conditions = new ArrayList<>();

  public static FieldBuilder where(String field) {
    Query query = new Query();
    return new FieldBuilder(field, query);
  }

  public FieldBuilder and(String field) {
    return new FieldBuilder(field, this);
  }

  void addCondition(Condition condition) {
    conditions.add(condition);
  }

  public List<Condition> getConditions() {
    return conditions;
  }

  public String toWhereClause() {
    return conditions.stream().map(Condition::toSqlFragment).collect(Collectors.joining(" AND "));
  }

  public List<Object> collectValues() {
    return conditions.stream().flatMap(c -> c.getValues().stream()).toList();
  }
}
