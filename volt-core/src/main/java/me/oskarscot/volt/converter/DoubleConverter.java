package me.oskarscot.volt.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.jetbrains.annotations.NotNull;

public class DoubleConverter implements BidirectionalTypeConverter<Double> {

  @Override
  public void write(@NotNull PreparedStatement stmt, int index, Double value) throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.DOUBLE);
    } else {
      stmt.setDouble(index, value);
    }
  }

  @Override
  public Double read(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
    double value = rs.getDouble(column);
    return rs.wasNull() ? null : value;
  }
}
