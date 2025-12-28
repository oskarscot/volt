package me.oskarscot.volt.query;

import java.util.List;

public abstract class Condition {

  protected final String field;
  protected final Operator operator;

  protected Condition(String field, Operator operator) {
    this.field = field;
    this.operator = operator;
  }

  public String getField() {
    return field;
  }

  public Operator getOperator() {
    return operator;
  }

  public abstract String toSqlFragment();

  public abstract List<Object> getValues();
}
