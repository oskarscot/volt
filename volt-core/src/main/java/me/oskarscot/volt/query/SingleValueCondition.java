package me.oskarscot.volt.query;

import java.util.List;

public class SingleValueCondition extends Condition {
    private final Object value;

    public SingleValueCondition(String field, Operator operator, Object value) {
        super(field, operator);
        this.value = value;
    }

    @Override
    public String toSqlFragment() {
        return field + " " + operator.sql() + " ?";
    }

    @Override
    public List<Object> getValues() {
        return List.of(value);
    }
}