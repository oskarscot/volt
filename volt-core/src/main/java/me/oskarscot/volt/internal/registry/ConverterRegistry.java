package me.oskarscot.volt.internal.registry;

import me.oskarscot.volt.converter.BidirectionalTypeConverter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class ConverterRegistry {

    private final Map<Class<?>, BidirectionalTypeConverter<?>> converters = new HashMap<>();

    public void register(Class<?> type, BidirectionalTypeConverter<?> converter) {
        converters.put(type, converter);
    }

    public BidirectionalTypeConverter<?> get(Class<?> type) {
        return converters.get(type);
    }

    @SuppressWarnings("unchecked")
    public void write(PreparedStatement stmt, int index, Object value, Class<?> type) throws SQLException {
        BidirectionalTypeConverter<Object> converter = (BidirectionalTypeConverter<Object>) converters.get(type); // this is checked when registering the converter
        if (converter != null) {
            converter.write(stmt, index, value);
        } else {
            stmt.setObject(index, value);
        }
    }

    public Object read(ResultSet rs, String column, Class<?> type) throws SQLException {
        BidirectionalTypeConverter<?> converter = converters.get(type);
        if (converter != null) {
            return converter.read(rs, column);
        } else {
            return rs.getObject(column);
        }
    }
}
