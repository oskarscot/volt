package me.oskarscot.volt.internal;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;

public final class ConnectionManager {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final HikariDataSource hikariDataSource;

    ConnectionManager(HikariDataSource hikariDataSource) {
        this.hikariDataSource = hikariDataSource;
    }

    public Connection acquire() {
        try {
            return this.hikariDataSource.getConnection();
        } catch (SQLException e) {
            logger.error("Unable to acquire connection", e);
        }
        return null;
    }

    public void release(Connection connection) {
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
