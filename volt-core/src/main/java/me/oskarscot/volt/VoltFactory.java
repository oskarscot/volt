package me.oskarscot.volt;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.oskarscot.volt.internal.VoltImpl;

public final class VoltFactory {

  private VoltFactory() {}

  public static Volt createVolt(HikariConfig config) {
    HikariDataSource dataSource = new HikariDataSource(config);
    return new VoltImpl(dataSource);
  }
}
