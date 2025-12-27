package me.oskarscot.volt.converter;

import java.sql.*;
import java.time.LocalDate;

public class LocalDateConverter implements BidirectionalTypeConverter<LocalDate> {

    @Override
    public void write(PreparedStatement stmt, int index, LocalDate value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.DATE);
        } else {
            stmt.setDate(index, Date.valueOf(value));
        }
    }

    @Override
    public LocalDate read(ResultSet rs, String column) throws SQLException {
        Date date = rs.getDate(column);
        return date != null ? date.toLocalDate() : null;
    }
}
