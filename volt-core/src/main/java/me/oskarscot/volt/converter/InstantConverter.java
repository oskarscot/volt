package me.oskarscot.volt.converter;

import java.sql.*;
import java.time.Instant;
import org.jetbrains.annotations.NotNull;

public class InstantConverter implements BidirectionalTypeConverter<Instant> {

  @Override
  public void write(@NotNull PreparedStatement stmt, int index, Instant value) throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.TIMESTAMP);
    } else {
      stmt.setTimestamp(index, Timestamp.from(value));
    }
  }

  @Override
  public Instant read(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
    Timestamp ts = rs.getTimestamp(column);
    return ts != null ? ts.toInstant() : null;
  }
}
