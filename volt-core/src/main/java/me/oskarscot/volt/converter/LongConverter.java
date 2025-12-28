package me.oskarscot.volt.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.jetbrains.annotations.NotNull;

public class LongConverter implements BidirectionalTypeConverter<Long> {

  @Override
  public void write(@NotNull PreparedStatement stmt, int index, Long value) throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.BIGINT);
    } else {
      stmt.setLong(index, value);
    }
  }

  @Override
  public Long read(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
    long value = rs.getLong(column);
    return rs.wasNull() ? null : value;
  }
}
