package me.oskarscot.volt.entity;

import me.oskarscot.volt.annotation.Entity;
import me.oskarscot.volt.annotation.Identifier;
import me.oskarscot.volt.annotation.NamedField;
import me.oskarscot.volt.exception.VoltException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class EntityDefinitionFactory {

    public static <T> EntityDefinition<T> fromClass(Class<T> clazz) {
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

    private static String resolveColumnName(Field field) {
        NamedField annotation = field.getDeclaredAnnotation(NamedField.class);
        return (annotation != null) ? annotation.name() : field.getName();
    }
}
