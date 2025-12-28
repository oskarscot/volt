package me.oskarscot.volt.internal.registry;

import me.oskarscot.volt.exception.VoltException;
import org.jetbrains.annotations.ApiStatus.Internal;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.oskarscot.volt.annotation.Entity;
import me.oskarscot.volt.entity.EntityDefinition;
import me.oskarscot.volt.entity.EntityDefinitionFactory;
import me.oskarscot.volt.entity.FieldDefinition;
import me.oskarscot.volt.util.ClassUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for entity definitions.
 */
@Internal
public final class EntityRegistry {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final ConcurrentHashMap<Class<?>, EntityDefinition<?>> entityMap = new ConcurrentHashMap<>();
  private final ConverterRegistry converterRegistry;

  @Internal
  public EntityRegistry(@NotNull ConverterRegistry converterRegistry) {
    this.converterRegistry = converterRegistry;
  }

  /**
   * Registers an entity class.
   *
   * @param entityClass the entity class to register
   * @throws VoltException if the class is not a valid entity
   */
  public <T> void registerEntity(@NotNull Class<T> entityClass) {
    Objects.requireNonNull(entityClass, "Entity class cannot be null");

    validateEntityClass(entityClass);

    if (isRegistered(entityClass)) {
      logger.warn("Entity {} is already registered", entityClass.getName());
      return;
    }

    EntityDefinition<T> definition = EntityDefinitionFactory.fromClass(entityClass);
    warnMissingConverters(definition);
    entityMap.put(entityClass, definition);

    logger.debug("Registered entity: {}", entityClass.getName());
  }

  /**
   * Gets the entity definition for a class.
   *
   * @param entityClass the entity class
   * @return the entity definition, or {@code null} if not registered
   */
  @Nullable
  public EntityDefinition<?> get(@NotNull Class<?> entityClass) {
    Objects.requireNonNull(entityClass, "Entity class cannot be null");
    return entityMap.get(entityClass);
  }

  /**
   * Checks if an entity class is registered.
   *
   * @param entityClass the entity class
   * @return {@code true} if registered, {@code false} otherwise
   */
  public boolean isRegistered(@NotNull Class<?> entityClass) {
    Objects.requireNonNull(entityClass, "Entity class cannot be null");
    return entityMap.containsKey(entityClass);
  }

  private <T> void validateEntityClass(@NotNull Class<T> entityClass) {
    if (entityClass.getDeclaredAnnotation(Entity.class) == null) {
      throw new VoltException("Entity " + entityClass.getName() + " has no @Entity annotation");
    }

    if (!ClassUtil.hasPublicNoArgConstructor(entityClass)) {
      throw new VoltException("Entity " + entityClass.getName() + " has no public no-arg constructor");
    }
  }

  private void warnMissingConverters(@NotNull EntityDefinition<?> definition) {
    Class<?> pkType = definition.getPrimaryKey().getField().getType();
    if (converterRegistry.get(pkType) == null) {
      logger.warn("No converter for type {}. Falling back to native JDBC.", pkType.getName());
    }

    for (FieldDefinition field : definition.getFields()) {
      Class<?> fieldType = field.getField().getType();
      if (converterRegistry.get(fieldType) == null) {
        logger.warn("No converter for type {}. Falling back to native JDBC.", fieldType.getName());
      }
    }
  }
}
