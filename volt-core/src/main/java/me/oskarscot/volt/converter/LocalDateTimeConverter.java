package me.oskarscot.volt.converter;

import java.sql.*;
import java.time.LocalDateTime;

public class LocalDateTimeConverter implements BidirectionalTypeConverter<LocalDateTime> {

    @Override
    public void write(PreparedStatement stmt, int index, LocalDateTime value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.TIMESTAMP);
        } else {
            stmt.setTimestamp(index, Timestamp.valueOf(value));
        }
    }

    @Override
    public LocalDateTime read(ResultSet rs, String column) throws SQLException {
        Timestamp ts = rs.getTimestamp(column);
        return ts != null ? ts.toLocalDateTime() : null;
    }
}
