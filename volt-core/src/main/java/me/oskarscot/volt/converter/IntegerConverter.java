package me.oskarscot.volt.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.jetbrains.annotations.NotNull;

public class IntegerConverter implements BidirectionalTypeConverter<Integer> {

  @Override
  public void write(@NotNull PreparedStatement stmt, int index, Integer value) throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.INTEGER);
    } else {
      stmt.setInt(index, value);
    }
  }

  @Override
  public Integer read(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
    int value = rs.getInt(column);
    return rs.wasNull() ? null : value;
  }
}
