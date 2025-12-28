package me.oskarscot.volt.entity;

import me.oskarscot.volt.annotation.Entity;
import me.oskarscot.volt.annotation.Identifier;
import me.oskarscot.volt.annotation.NamedField;
import me.oskarscot.volt.exception.VoltException;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * Factory for creating {@link EntityDefinition} instances from annotated classes.
 */
public final class EntityDefinitionFactory {

  private EntityDefinitionFactory() { }

  /**
   * Creates an entity definition from an annotated class.
   *
   * <p>The class must:</p>
   * <ul>
   *     <li>Be annotated with {@link Entity} specifying the table name</li>
   *     <li>Have exactly one field annotated with {@link Identifier}</li>
   *     <li>Have a public no-arg constructor</li>
   * </ul>
   *
   * <h2>Example</h2>
   * <pre>{@code
   * @Entity("users")
   * public class User {
   *
   *     @Identifier(type = PrimaryKeyType.NUMBER, generated = true)
   *     private Long id;
   *
   *     @NamedField(name = "user_name")
   *     private String name;
   *
   *     private String email;
   *
   *     public User() {}
   * }
   *
   * EntityDefinition<User> definition = EntityDefinitionFactory.fromClass(User.class);
   * }</pre>
   *
   * @param clazz the entity class to process
   * @param <T> the entity type
   * @return the entity definition
   * @throws VoltException if the class is not a valid entity
   */
  @NotNull
  public static <T> EntityDefinition<T> fromClass(@NotNull Class<T> clazz) {
    Entity entityAnnotation = clazz.getDeclaredAnnotation(Entity.class);
    if (entityAnnotation == null) {
      throw new VoltException("Class " + clazz.getName() + " is not annotated with @Entity");
    }

    String tableName = entityAnnotation.value();
    if (tableName == null || tableName.isEmpty()) {
      throw new VoltException("Entity " + clazz.getName() + " has no table name");
    }

    PrimaryKey primaryKey = null;
    List<FieldDefinition> fields = new ArrayList<>();

    for (Field field : clazz.getDeclaredFields()) {
      String columnName = resolveColumnName(field);

      if (field.isAnnotationPresent(Identifier.class)) {
        if (primaryKey != null) {
          throw new VoltException("Entity " + clazz.getName() + " has multiple @Identifier fields");
        }
        Identifier annotation = field.getDeclaredAnnotation(Identifier.class);
        primaryKey = new PrimaryKey(columnName, field, annotation.type(), annotation.generated());
      } else {
        fields.add(new FieldDefinition(columnName, field));
      }
    }

    if (primaryKey == null) {
      throw new VoltException("Entity " + clazz.getName() + " has no @Identifier field");
    }

    return new EntityDefinition<>(clazz, tableName, primaryKey, fields);
  }

  @NotNull
  private static String resolveColumnName(@NotNull Field field) {
    NamedField annotation = field.getDeclaredAnnotation(NamedField.class);
    return (annotation != null) ? annotation.name() : field.getName();
  }
}
