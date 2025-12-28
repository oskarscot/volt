package me.oskarscot.volt.internal.registry;

import me.oskarscot.volt.converter.BidirectionalTypeConverter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for type converters.
 *
 * <p><b>Internal class — not part of the public API.</b></p>
 */
@Internal
public final class ConverterRegistry {

  private final Map<Class<?>, BidirectionalTypeConverter<?>> converters = new ConcurrentHashMap<>();

  /**
   * Registers a converter for a type.
   *
   * @param type the Java type to convert
   * @param converter the converter implementation
   * @param <T> the type being converted
   */
  public <T> void register(@NotNull Class<T> type, @NotNull BidirectionalTypeConverter<T> converter) {
    converters.put(type, converter);
  }

  /**
   * Gets the converter for a type.
   *
   * @param type the Java type
   * @return the converter, or {@code null} if none registered
   */
  @Nullable
  public BidirectionalTypeConverter<?> get(@NotNull Class<?> type) {
    return converters.get(type);
  }

  /**
   * Checks if a converter is registered for a type.
   *
   * @param type the Java type
   * @return {@code true} if a converter is registered, {@code false} otherwise
   */
  public boolean hasConverter(@NotNull Class<?> type) {
    return converters.containsKey(type);
  }

  /**
   * Writes a value to a prepared statement using the appropriate converter.
   *
   * <p>Falls back to {@link PreparedStatement#setObject(int, Object)} if no converter is registered.</p>
   *
   * @param stmt the prepared statement
   * @param index the parameter index (1-based)
   * @param value the value to write
   * @param type the Java type of the value
   * @throws SQLException if a database error occurs
   */
  @SuppressWarnings("unchecked")
  public void write(@NotNull PreparedStatement stmt, int index, @Nullable Object value, @NotNull Class<?> type)
      throws SQLException {
    BidirectionalTypeConverter<Object> converter = (BidirectionalTypeConverter<Object>) converters.get(type);
    if (converter != null) {
      converter.write(stmt, index, value);
    } else {
      stmt.setObject(index, value);
    }
  }

  /**
   * Reads a value from a result set using the appropriate converter.
   *
   * <p>Falls back to {@link ResultSet#getObject(String)} if no converter is registered.</p>
   *
   * @param rs the result set
   * @param column the column name
   * @param type the expected Java type
   * @return the value, or {@code null} if the column was NULL
   * @throws SQLException if a database error occurs
   */
  @Nullable
  public Object read(@NotNull ResultSet rs, @NotNull String column, @NotNull Class<?> type)
      throws SQLException {
    BidirectionalTypeConverter<?> converter = converters.get(type);
    if (converter != null) {
      return converter.read(rs, column);
    } else {
      return rs.getObject(column);
    }
  }
}