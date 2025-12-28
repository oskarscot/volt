package me.oskarscot.volt.internal;

import com.zaxxer.hikari.HikariDataSource;
import me.oskarscot.volt.converter.*;
import me.oskarscot.volt.converter.*;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.oskarscot.volt.Result;
import me.oskarscot.volt.Transaction;
import me.oskarscot.volt.Volt;
import me.oskarscot.volt.exception.VoltError;
import me.oskarscot.volt.internal.registry.ConverterRegistry;
import me.oskarscot.volt.internal.registry.EntityRegistry;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public final class VoltImpl implements Volt {

  private final Logger logger = LoggerFactory.getLogger("Volt");

  private final ConnectionManager connectionManager;
  private final EntityRegistry entityRegistry;
  private final ConverterRegistry converterRegistry;

  @Internal
  public VoltImpl(HikariDataSource dataSource) {
    this.logger.info("Initializing Volt...");
    this.connectionManager = new ConnectionManager(dataSource);
    this.converterRegistry = new ConverterRegistry();
    this.entityRegistry = new EntityRegistry(converterRegistry);
    registerDefaultConverters();
    this.logger.info("Initialized Volt");
  }

  @Override
  public void registerEntity(@NotNull Class<?> entityClass) {
    this.entityRegistry.registerEntity(entityClass);
  }

  @Override
  public <T> void registerConverter(
      @NotNull Class<T> type, @NotNull BidirectionalTypeConverter<T> converter) {
    this.converterRegistry.register(type, converter);
  }

  @Override
  public @NotNull Transaction beginTransaction() {
    Connection connection = connectionManager.acquire();

    if (connection == null) {
      throw new RuntimeException("Failed to acquire connection from pool");
    }

    try {
      connection.setAutoCommit(false);
      return new TransactionImpl(entityRegistry, converterRegistry, connectionManager, connection);
    } catch (SQLException e) {
      connectionManager.release(connection);
      throw new RuntimeException("Failed to begin transaction: " + e.getMessage(), e);
    }
  }

  @Override
  public boolean testConnection() {
    this.logger.info("Testing connection...");
    Connection test = this.connectionManager.acquire();
    if (test == null) {
      return false;
    }
    this.connectionManager.release(test);
    this.logger.info("Test connection acquired!");
    return true;
  }

  @Override
  public <T> @NotNull Result<T, VoltError> findById(@NotNull Class<T> type, @NotNull Object id) {
    try (Transaction tx = beginTransaction()) {
      Result<T, VoltError> result = tx.findById(type, id);
      if (result.isSuccess()) {
        tx.commit();
      }
      return result;
    }
  }

  @Override
  public <T> @NotNull Result<T, VoltError> save(@NotNull T entity) {
    try (Transaction tx = beginTransaction()) {
      Result<T, VoltError> result = tx.save(entity);
      if (result.isSuccess()) {
        tx.commit();
      }
      return result;
    }
  }

  @Override
  public @NotNull <T> Result<Void, VoltError> delete(@NotNull T entity) {
    try (Transaction tx = beginTransaction()) {
      Result<Void, VoltError> result = tx.delete(entity);
      if (result.isSuccess()) {
        tx.commit();
      }
      return result;
    }
  }

  private void registerDefaultConverters() {
    this.converterRegistry.register(String.class, new StringConverter());

    this.converterRegistry.register(Long.class, new LongConverter());
    this.converterRegistry.register(long.class, new LongConverter());
    this.converterRegistry.register(Integer.class, new IntegerConverter());
    this.converterRegistry.register(int.class, new IntegerConverter());
    this.converterRegistry.register(Double.class, new DoubleConverter());
    this.converterRegistry.register(double.class, new DoubleConverter());
    this.converterRegistry.register(BigDecimal.class, new BigDecimalConverter());

    this.converterRegistry.register(Boolean.class, new BooleanConverter());
    this.converterRegistry.register(boolean.class, new BooleanConverter());

    this.converterRegistry.register(Instant.class, new InstantConverter());
    this.converterRegistry.register(LocalDate.class, new LocalDateConverter());
    this.converterRegistry.register(LocalDateTime.class, new LocalDateTimeConverter());

    this.converterRegistry.register(UUID.class, new UUIDConverter());
  }
}
