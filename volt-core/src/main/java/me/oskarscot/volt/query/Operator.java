package me.oskarscot.volt.query;

public enum Operator {
    EQ("="),
    NOT_EQ("<>"),
    GT(">"),
    GTE(">="),
    LT("<"),
    LTE("<="),
    LIKE("LIKE"),
    NOT_LIKE("NOT LIKE"),
    IN("IN"),
    NOT_IN("NOT IN"),
    BETWEEN("BETWEEN"),
    IS_NULL("IS NULL"),
    NOT_NULL("IS NOT NULL");

    private final String sql;

    Operator(String sql) {
        this.sql = sql;
    }

    public String sql() {
        return sql;
    }
}
