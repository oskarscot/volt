package me.oskarscot.volt;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * A result type that represents either a successful value or an error.
 *
 * <p>Inspired by Rust's {@code Result} type, this class provides a way to handle
 * operations that may fail without using exceptions for control flow.</p>
 *
 * <h2>Usage</h2>
 * <pre>{@code
 * Result<User, VoltError> result = volt.findById(User.class, 1L);
 *
 * if (result.isSuccess()) {
 *     User user = result.getValue();
 *     System.out.println("Found: " + user.getName());
 * } else {
 *     VoltError error = result.getError();
 *     System.out.println("Error: " + error.message());
 * }
 * }</pre>
 *
 * @param <T> the type of the success value
 * @param <E> the type of the error value
 */
public final class Result<T, E> {

  private final T value;
  private final E error;
  private final boolean success;

  private Result(@Nullable T value, @Nullable E error, boolean success) {
    this.value = value;
    this.error = error;
    this.success = success;
  }

  /**
   * Creates a successful result with the given value.
   *
   * @param value the success value (may be null for {@code Result<Void, E>})
   * @param <T> the type of the success value
   * @param <E> the type of the error value
   * @return a successful result containing the value
   */
  @NotNull
  public static <T, E> Result<T, E> okay(@Nullable T value) {
    return new Result<>(value, null, true);
  }

  /**
   * Creates a failed result with the given error.
   *
   * @param error the error value
   * @param <T> the type of the success value
   * @param <E> the type of the error value
   * @return a failed result containing the error
   */
  @NotNull
  public static <T, E> Result<T, E> failure(@NotNull E error) {
    return new Result<>(null, error, false);
  }

  /**
   * Returns {@code true} if this result represents a success.
   *
   * @return {@code true} if successful, {@code false} otherwise
   */
  public boolean isSuccess() {
    return success;
  }

  /**
   * Returns {@code true} if this result represents a failure.
   *
   * @return {@code true} if failed, {@code false} otherwise
   */
  public boolean isFailure() {
    return !success;
  }

  /**
   * Returns the success value.
   *
   * @return the success value
   * @throws IllegalStateException if this result is a failure
   */
  @Nullable
  public T getValue() {
    if (!success) {
      throw new IllegalStateException("Cannot get value from failed result");
    }
    return value;
  }

  /**
   * Returns the error value.
   *
   * @return the error value
   * @throws IllegalStateException if this result is a success
   */
  @NotNull
  public E getError() {
    if (success) {
      throw new IllegalStateException("Cannot get error from successful result");
    }
    return error;
  }
}