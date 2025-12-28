package me.oskarscot.volt;

import me.oskarscot.volt.exception.VoltError;
import me.oskarscot.volt.query.Query;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Represents a database transaction.
 *
 * <p>A transaction groups multiple database operations into a single atomic unit.
 * Either all operations succeed (on {@link #commit()}), or all are undone (on {@link #rollback()}).</p>
 *
 * <p>Transactions must be used with try-with-resources to ensure proper cleanup:</p>
 * <pre>{@code
 * try (Transaction tx = volt.beginTransaction()) {
 *     tx.save(user);
 *     tx.save(order);
 *     tx.commit();
 * }
 * }</pre>
 *
 * <p>If {@link #commit()} is not called before closing, the transaction will automatically roll back.</p>
 *
 * @see Volt#beginTransaction()
 */
public interface Transaction extends AutoCloseable {

  /**
   * Saves an entity to the database.
   *
   * <p>Uses UPSERT semantics — inserts a new row if one doesn't exist,
   * or updates the existing row if it does.</p>
   *
   * @param entity the entity to save
   * @param <T> the entity type
   * @return a result containing the saved entity, or an error if the operation failed
   */
  @NotNull
  <T> Result<T, VoltError> save(@NotNull T entity);

  /**
   * Finds an entity by its primary key.
   *
   * @param type the entity class
   * @param id the primary key value
   * @param <T> the entity type
   * @return a result containing the entity, or an error if not found
   */
  @NotNull
  <T> Result<T, VoltError> findById(@NotNull Class<T> type, @NotNull Object id);

  /**
   * Finds the first entity matching a field value.
   *
   * @param type the entity class
   * @param field the field name to match
   * @param value the value to match
   * @param <T> the entity type
   * @return a result containing an optional entity (empty if not found), or an error on failure
   */
  @NotNull
  <T> Result<Optional<T>, VoltError> findFirstBy(@NotNull Class<T> type, @NotNull String field, @NotNull Object value);

  /**
   * Finds exactly one entity matching a field value.
   *
   * <p>Returns an error if zero or more than one entity matches.</p>
   *
   * @param type the entity class
   * @param field the field name to match
   * @param value the value to match
   * @param <T> the entity type
   * @return a result containing the entity, or an error if not found or multiple found
   */
  @NotNull
  <T> Result<T, VoltError> findOneBy(@NotNull Class<T> type, @NotNull String field, @NotNull Object value);

  /**
   * Finds all entities matching a field value.
   *
   * @param type the entity class
   * @param field the field name to match
   * @param value the value to match
   * @param <T> the entity type
   * @return a result containing a list of entities (may be empty), or an error on failure
   */
  @NotNull
  <T> Result<List<T>, VoltError> findAllBy(@NotNull Class<T> type, @NotNull String field, @NotNull Object value);

  /**
   * Finds all entities of a given type.
   *
   * @param type the entity class
   * @param <T> the entity type
   * @return a result containing a list of all entities (may be empty), or an error on failure
   */
  @NotNull
  <T> Result<List<T>, VoltError> findAll(@NotNull Class<T> type);

  /**
   * Finds the first entity matching a query.
   *
   * <pre>{@code
   * Query query = Query.where("active").eq(true);
   * Result<Optional<User>, VoltError> result = tx.findFirstBy(User.class, query);
   * }</pre>
   *
   * @param type the entity class
   * @param query the query to execute
   * @param <T> the entity type
   * @return a result containing an optional entity (empty if not found), or an error on failure
   * @see Query
   */
  @NotNull
  <T> Result<Optional<T>, VoltError> findFirstBy(@NotNull Class<T> type, @NotNull Query query);

  /**
   * Finds exactly one entity matching a query.
   *
   * <p>Returns an error if zero or more than one entity matches.</p>
   *
   * <pre>{@code
   * Query query = Query.where("email").eq("john@example.com");
   * Result<User, VoltError> result = tx.findOneBy(User.class, query);
   * }</pre>
   *
   * @param type the entity class
   * @param query the query to execute
   * @param <T> the entity type
   * @return a result containing the entity, or an error if not found or multiple found
   * @see Query
   */
  @NotNull
  <T> Result<T, VoltError> findOneBy(@NotNull Class<T> type, @NotNull Query query);

  /**
   * Finds all entities matching a query.
   *
   * <pre>{@code
   * Query query = Query.where("active").eq(true).and("age").gte(18);
   * Result<List<User>, VoltError> result = tx.findAllBy(User.class, query);
   * }</pre>
   *
   * @param type the entity class
   * @param query the query to execute
   * @param <T> the entity type
   * @return a result containing a list of entities (may be empty), or an error on failure
   * @see Query
   */
  @NotNull
  <T> Result<List<T>, VoltError> findAllBy(@NotNull Class<T> type, @NotNull Query query);

  /**
   * Deletes an entity from the database.
   *
   * @param entity the entity to delete (must have a non-null primary key)
   * @param <T> the entity type
   * @return a result indicating success, or an error if the entity was not found
   */
  @NotNull
  <T> Result<Void, VoltError> delete(@NotNull T entity);

  /**
   * Deletes an entity by its primary key.
   *
   * @param type the entity class
   * @param id the primary key value
   * @param <T> the entity type
   * @return a result indicating success, or an error if the entity was not found
   */
  @NotNull
  <T> Result<Void, VoltError> deleteById(@NotNull Class<T> type, @NotNull Object id);

  /**
   * Commits the transaction, making all changes permanent.
   *
   * <p>After committing, the transaction should be closed.</p>
   *
   * @return a result indicating success, or an error if the commit failed
   */
  @NotNull
  Result<Void, VoltError> commit();

  /**
   * Rolls back the transaction, undoing all changes.
   *
   * <p>After rolling back, the transaction should be closed.</p>
   *
   * @return a result indicating success, or an error if the rollback failed
   */
  @NotNull
  Result<Void, VoltError> rollback();

  /**
   * Closes the transaction and releases the database connection.
   *
   * <p>If the transaction has not been committed, it will be rolled back automatically.</p>
   *
   * <p>This method is called automatically when using try-with-resources.</p>
   */
  @Override
  void close();
}