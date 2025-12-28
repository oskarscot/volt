package me.oskarscot.volt.converter;

import java.sql.*;
import java.time.LocalDate;
import org.jetbrains.annotations.NotNull;

public class LocalDateConverter implements BidirectionalTypeConverter<LocalDate> {

  @Override
  public void write(@NotNull PreparedStatement stmt, int index, LocalDate value)
      throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.DATE);
    } else {
      stmt.setDate(index, Date.valueOf(value));
    }
  }

  @Override
  public LocalDate read(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
    Date date = rs.getDate(column);
    return date != null ? date.toLocalDate() : null;
  }
}
