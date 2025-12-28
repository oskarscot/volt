package me.oskarscot.volt.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import me.oskarscot.volt.Volt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Converts values between Java types and database types.
 *
 * <p>Implementations handle both writing values to a {@link PreparedStatement}
 * and reading values from a {@link ResultSet}.</p>
 *
 * <h2>Example Implementation</h2>
 * <pre>{@code
 * public class InstantConverter implements BidirectionalTypeConverter<Instant> {
 *
 *     @Override
 *     public void write(PreparedStatement stmt, int index, Instant value) throws SQLException {
 *         if (value == null) {
 *             stmt.setNull(index, Types.TIMESTAMP);
 *         } else {
 *             stmt.setTimestamp(index, Timestamp.from(value));
 *         }
 *     }
 *
 *     @Override
 *     public Instant read(ResultSet rs, String column) throws SQLException {
 *         Timestamp ts = rs.getTimestamp(column);
 *         return ts != null ? ts.toInstant() : null;
 *     }
 * }
 * }</pre>
 *
 * <h2>Registering Custom Converters</h2>
 * <pre>{@code
 * volt.registerConverter(Money.class, new MoneyConverter());
 * }</pre>
 *
 * @param <T> the Java type this converter handles
 * @see Volt#registerConverter(Class, BidirectionalTypeConverter)
 */
public interface BidirectionalTypeConverter<T> {

  /**
   * Writes a value to a prepared statement.
   *
   * <p>Implementations should handle null values appropriately,
   * typically by calling {@link PreparedStatement#setNull(int, int)}.</p>
   *
   * @param stmt the prepared statement to write to
   * @param index the parameter index (1-based)
   * @param value the value to write (may be null)
   * @throws SQLException if a database error occurs
   */
  void write(@NotNull PreparedStatement stmt, int index, @Nullable T value) throws SQLException;

  /**
   * Reads a value from a result set.
   *
   * <p>Implementations should handle SQL NULL values appropriately,
   * typically by returning {@code null}.</p>
   *
   * @param rs the result set to read from
   * @param column the column name to read
   * @return the value, or {@code null} if the column was NULL
   * @throws SQLException if a database error occurs
   */
  @Nullable
  T read(@NotNull ResultSet rs, @NotNull String column) throws SQLException;
}
