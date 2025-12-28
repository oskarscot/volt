package me.oskarscot.volt.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import org.jetbrains.annotations.NotNull;

public class StringConverter implements BidirectionalTypeConverter<String> {

  @Override
  public void write(@NotNull PreparedStatement stmt, int index, String value) throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.VARCHAR);
    } else {
      stmt.setString(index, value);
    }
  }

  @Override
  public String read(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
    return rs.getString(column);
  }
}
