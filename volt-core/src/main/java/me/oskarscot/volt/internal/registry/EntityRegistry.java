package me.oskarscot.volt.internal.registry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import me.oskarscot.volt.annotation.Entity;
import me.oskarscot.volt.entity.EntityDefinition;
import me.oskarscot.volt.entity.EntityDefinitionFactory;
import me.oskarscot.volt.entity.FieldDefinition;
import me.oskarscot.volt.util.ClassUtil;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class EntityRegistry {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final ConcurrentHashMap<Class<?>, EntityDefinition<?>>  entityMap = new ConcurrentHashMap<>();
    private final ConverterRegistry converterRegistry;

    public EntityRegistry(ConverterRegistry converterRegistry) {
        this.converterRegistry = converterRegistry;
    }

    public <T> void registerEntity(Class<T> entity) {
        //TODO: Refactor Validations, perhaps use apache validations
        Objects.requireNonNull(entity, "Entity class cannot be null");
        if(entity.getDeclaredAnnotation(Entity.class) == null) {
            logger.error("Entity {} has no @Entity annotation", entity.getName());
            return;
        }
        if(!ClassUtil.hasPublicNoArgConstructor(entity)) {
            logger.error("Entity {} has no public no-arg constructor", entity.getName());
            return;
        }
        if(entityMap.containsKey(entity)) {
            logger.warn("Entity {} is already registered", entity.getName());
            return;
        }
        EntityDefinition<T> entityDefinition = EntityDefinitionFactory.fromClass(entity);
        checkConverters(entityDefinition);
        entityMap.put(entity, entityDefinition);
    }

    public void removeEntity(Class<?> entity) {
        Objects.requireNonNull(entity, "Entity class cannot be null");
        if(entity.getDeclaredAnnotation(Entity.class) == null) {
            logger.error("Entity {} has no @Entity annotation", entity.getName());
            return;
        }
        if(!entityMap.containsKey(entity)) {
            logger.warn("Entity {} is not registered", entity.getName());
            return;
        }
        entityMap.remove(entity);
    }

    public EntityDefinition<?> get(Class<?> entity) {
        Objects.requireNonNull(entity, "Entity class cannot be null");
        return entityMap.get(entity);
    }

    private void checkConverters(EntityDefinition<?> definition) {
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
