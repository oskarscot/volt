package me.oskarscot.volt.converter;

import java.sql.*;
import java.time.LocalDateTime;
import org.jetbrains.annotations.NotNull;

public class LocalDateTimeConverter implements BidirectionalTypeConverter<LocalDateTime> {

  @Override
  public void write(@NotNull PreparedStatement stmt, int index, LocalDateTime value)
      throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.TIMESTAMP);
    } else {
      stmt.setTimestamp(index, Timestamp.valueOf(value));
    }
  }

  @Override
  public LocalDateTime read(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
    Timestamp ts = rs.getTimestamp(column);
    return ts != null ? ts.toLocalDateTime() : null;
  }
}
