package me.oskarscot.volt.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.jetbrains.annotations.NotNull;

public class BooleanConverter implements BidirectionalTypeConverter<Boolean> {

  @Override
  public void write(@NotNull PreparedStatement stmt, int index, Boolean value) throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.BOOLEAN);
    } else {
      stmt.setBoolean(index, value);
    }
  }

  @Override
  public Boolean read(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
    boolean value = rs.getBoolean(column);
    return rs.wasNull() ? null : value;
  }
}
