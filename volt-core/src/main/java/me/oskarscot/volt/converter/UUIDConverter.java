package me.oskarscot.volt.converter;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;
import org.jetbrains.annotations.NotNull;

public class UUIDConverter implements BidirectionalTypeConverter<UUID> {

  @Override
  public void write(@NotNull PreparedStatement stmt, int index, UUID value) throws SQLException {
    if (value == null) {
      stmt.setNull(index, Types.OTHER);
    } else {
      stmt.setObject(index, value);
    }
  }

  @Override
  public UUID read(@NotNull ResultSet rs, @NotNull String column) throws SQLException {
    return rs.getObject(column, UUID.class);
  }
}
