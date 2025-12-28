package me.oskarscot.volt.query;

import java.util.List;

public class BetweenCondition extends Condition {

  private final Object lower;
  private final Object upper;

  public BetweenCondition(String field, Object lower, Object upper) {
    super(field, Operator.BETWEEN);
    this.lower = lower;
    this.upper = upper;
  }

  @Override
  public String toSqlFragment() {
    return field + " BETWEEN ? AND ?";
  }

  @Override
  public List<Object> getValues() {
    return List.of(lower, upper);
  }
}
