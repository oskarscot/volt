package me.oskarscot.volt.internal.builders;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public interface EntitySqlBuilder<T> extends SqlBuilder {
    void bindValues(PreparedStatement stmt) throws SQLException, IllegalAccessException;
}