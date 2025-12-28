package me.oskarscot.volt;

import me.oskarscot.volt.converter.BidirectionalTypeConverter;
import me.oskarscot.volt.exception.VoltError;
import org.jetbrains.annotations.NotNull;

/**
 * Volt is a lightweight Java ORM.
 *
 * <p>The main entry point for interacting with the database. Use {@link VoltFactory} to create an instance.</p>
 *
 * <h2>Quick Start</h2>
 * <pre>{@code
 * HikariConfig config = new HikariConfig();
 * config.setJdbcUrl("jdbc:postgresql://localhost:5432/mydb");
 * config.setUsername("user");
 * config.setPassword("password");
 *
 * Volt volt = VoltFactory.createVolt(config);
 * volt.registerEntity(User.class);
 *
 * Result<User, VoltError> result = volt.save(new User("John"));
 * }</pre>
 *
 * @see VoltFactory
 * @see Transaction
 */
public interface Volt {

  /**
   * Registers an entity class with Volt.
   *
   * <p>The class must be annotated with {@link me.oskarscot.volt.annotation.Entity}
   * and have a public no-arg constructor.</p>
   *
   * @param entityClass the entity class to register
   * @throws IllegalArgumentException if the class is not a valid entity
   */
  void registerEntity(@NotNull Class<?> entityClass);

  /**
   * Registers a custom type converter.
   *
   * <p>Use this to add support for custom types not handled by default converters.</p>
   *
   * @param type the Java type to convert
   * @param converter the converter implementation
   * @param <T> the type being converted
   */
  <T> void registerConverter(@NotNull Class<T> type, @NotNull BidirectionalTypeConverter<T> converter);

  /**
   * Begins a new database transaction.
   *
   * <p>The returned transaction must be committed or rolled back, and should be used
   * with try-with-resources to ensure proper cleanup.</p>
   *
   * <pre>{@code
   * try (Transaction tx = volt.beginTransaction()) {
   *     tx.save(entity);
   *     tx.commit();
   * }
   * }</pre>
   *
   * @return a new transaction
   * @throws RuntimeException if a connection cannot be acquired
   */
  @NotNull
  Transaction beginTransaction();

  /**
   * Tests the database connection.
   *
   * @return {@code true} if a connection can be established, {@code false} otherwise
   */
  boolean testConnection();

  /**
   * Finds an entity by its primary key.
   *
   * <p>This is a convenience method that creates a short-lived transaction internally.
   * For multiple operations, use {@link #beginTransaction()} instead.</p>
   *
   * @param type the entity class
   * @param id the primary key value
   * @param <T> the entity type
   * @return a result containing the entity if found, or an error if not found
   */
  @NotNull
  <T> Result<T, VoltError> findById(@NotNull Class<T> type, @NotNull Object id);

  /**
   * Saves an entity to the database.
   *
   * <p>This method uses UPSERT semantics — it will insert a new row if one doesn't exist,
   * or update the existing row if it does.</p>
   *
   * <p>This is a convenience method that creates a short-lived transaction internally.
   * For multiple operations, use {@link #beginTransaction()} instead.</p>
   *
   * @param entity the entity to save
   * @param <T> the entity type
   * @return a result containing the saved entity, or an error if the operation failed
   */
  @NotNull
  <T> Result<T, VoltError> save(@NotNull T entity);

  /**
   * Deletes an entity to the database.
   *
   * <p>This is a convenience method that creates a short-lived transaction internally.
   * For multiple operations, use {@link #beginTransaction()} instead.</p>
   *
   * @param entity the entity to be removed
   * @param <T> the type of the entity
   * @return a result containing either Void indicating success, or an error if the operation failed
   */
  @NotNull
  <T> Result<Void, VoltError> delete(@NotNull T entity);
}