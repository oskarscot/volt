package me.oskarscot.volt.internal;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages database connections from the HikariCP connection pool.
 */
@Internal
public final class ConnectionManager {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  private final HikariDataSource hikariDataSource;

  @Internal
  ConnectionManager(@NotNull HikariDataSource hikariDataSource) {
    this.hikariDataSource = hikariDataSource;
  }

  /**
   * Acquires a connection from the pool.
   *
   * @return a database connection, or {@code null} if acquisition failed
   */
  @Nullable
  public Connection acquire() {
    try {
      return this.hikariDataSource.getConnection();
    } catch (SQLException e) {
      logger.error("Unable to acquire connection", e);
    }
    return null;
  }

  /**
   * Releases a connection back to the pool.
   *
   * <p>If the connection has an active transaction (auto-commit disabled),
   * it will be rolled back before closing.</p>
   *
   * @param connection the connection to release
   */
  public void release(@NotNull Connection connection) {
    try {
      if (!connection.getAutoCommit()) {
        logger.warn("Connection released with active transaction. Rolling back.");
        connection.rollback();
      }
    } catch (SQLException e) {
      logger.error("Failed to rollback connection", e);
    }

    try {
      connection.close();
    } catch (SQLException e) {
      logger.error("Failed to close connection", e);
    }
  }
}
