package me.oskarscot.volt.query;

import java.util.ArrayList;
import java.util.List;

public class FieldBuilder {
    private final String field;
    private final Query query;

    public FieldBuilder(String field, Query query) {
        this.field = field;
        this.query = query;
    }

    public Query eq(Object value) {
        query.addCondition(new SingleValueCondition(field, Operator.EQ, value));
        return query;
    }

    public Query notEq(Object value) {
        query.addCondition(new SingleValueCondition(field, Operator.NOT_EQ, value));
        return query;
    }

    public Query gt(Object value) {
        query.addCondition(new SingleValueCondition(field, Operator.GT, value));
        return query;
    }

    public Query gte(Object value) {
        query.addCondition(new SingleValueCondition(field, Operator.GTE, value));
        return query;
    }

    public Query lt(Object value) {
        query.addCondition(new SingleValueCondition(field, Operator.LT, value));
        return query;
    }

    public Query lte(Object value) {
        query.addCondition(new SingleValueCondition(field, Operator.LTE, value));
        return query;
    }

    public Query like(String pattern) {
        query.addCondition(new SingleValueCondition(field, Operator.LIKE, pattern));
        return query;
    }

    public Query notLike(String pattern) {
        query.addCondition(new SingleValueCondition(field, Operator.NOT_LIKE, pattern));
        return query;
    }

    public Query in(Object... values) {
        query.addCondition(new MultiValueCondition(field, Operator.IN, List.of(values)));
        return query;
    }

    public Query in(List<?> values) {
        query.addCondition(new MultiValueCondition(field, Operator.IN, new ArrayList<>(values)));
        return query;
    }

    public Query notIn(Object... values) {
        query.addCondition(new MultiValueCondition(field, Operator.NOT_IN, List.of(values)));
        return query;
    }

    public Query notIn(List<?> values) {
        query.addCondition(new MultiValueCondition(field, Operator.NOT_IN, new ArrayList<>(values)));
        return query;
    }

    public Query between(Object lower, Object upper) {
        query.addCondition(new BetweenCondition(field, lower, upper));
        return query;
    }

    public Query isNull() {
        query.addCondition(new NoValueCondition(field, Operator.IS_NULL));
        return query;
    }

    public Query notNull() {
        query.addCondition(new NoValueCondition(field, Operator.NOT_NULL));
        return query;
    }
}