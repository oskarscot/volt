package me.oskarscot.volt.query;

import java.util.List;
import java.util.stream.Collectors;

public class MultiValueCondition extends Condition {

  private final List<Object> values;

  public MultiValueCondition(String field, Operator operator, List<Object> values) {
    super(field, operator);
    this.values = values;
  }

  @Override
  public String toSqlFragment() {
    String placeholders = values.stream().map(v -> "?").collect(Collectors.joining(", "));
    return field + " " + operator.sql() + " (" + placeholders + ")";
  }

  @Override
  public List<Object> getValues() {
    return values;
  }
}
