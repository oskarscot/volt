package me.oskarscot.volt.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class UUIDConverter implements BidirectionalTypeConverter<UUID> {

    @Override
    public void write(PreparedStatement stmt, int index, UUID value) throws SQLException {
        if (value == null) {
            stmt.setNull(index, Types.OTHER);
        } else {
            stmt.setObject(index, value);
        }
    }

    @Override
    public UUID read(ResultSet rs, String column) throws SQLException {
        return rs.getObject(column, UUID.class);
    }
}