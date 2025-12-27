package me.oskarscot.volt.query;

import java.util.List;

public class NoValueCondition extends Condition {

    public NoValueCondition(String field, Operator operator) {
        super(field, operator);
    }

    @Override
    public String toSqlFragment() {
        return field + " " + operator.sql();
    }

    @Override
    public List<Object> getValues() {
        return List.of();
    }
}