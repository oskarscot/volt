package me.oskarscot.volt.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface BidirectionalTypeConverter<T> {

    void write(PreparedStatement stmt, int index, T value) throws SQLException;
    T read(ResultSet rs, String column) throws SQLException;

}
