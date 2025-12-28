package me.oskarscot.volt.converter;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.jetbrains.annotations.NotNull;

public class BigDecimalConverter implements BidirectionalTypeConverter<BigDecimal> {

  @Override
  public void write(@NotNull PreparedStatement stmt, int index, BigDecimal value)
      throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.DECIMAL);
    } else {
      stmt.setBigDecimal(index, value);
    }
  }

  @Override
  public BigDecimal read(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
    return rs.getBigDecimal(column);
  }
}
