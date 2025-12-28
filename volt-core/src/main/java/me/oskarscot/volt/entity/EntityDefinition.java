package me.oskarscot.volt.entity;

import java.util.ArrayList;
import java.util.List;
import org.jetbrains.annotations.ApiStatus.Internal;

public class EntityDefinition<T> {

  private final Class<T> clazz;
  private final String tableName;
  private final PrimaryKey primaryKey;
  private final List<FieldDefinition> fields;

  @Internal
  EntityDefinition(
      Class<T> clazz, String tableName, PrimaryKey primaryKey, List<FieldDefinition> fields) {
    this.clazz = clazz;
    this.tableName = tableName;
    this.primaryKey = primaryKey;
    this.fields = fields;
  }

  public Class<T> getClazz() {
    return clazz;
  }

  public String getTableName() {
    return tableName;
  }

  public PrimaryKey getPrimaryKey() {
    return primaryKey;
  }

  public List<FieldDefinition> getFields() {
    return fields;
  }

  public List<FieldDefinition> getAllFields() {
    List<FieldDefinition> all = new ArrayList<>();
    all.add(primaryKey);
    all.addAll(fields);
    return all;
  }

  @Override
  public String toString() {
    return "EntityDefinition{"
        + "clazz="
        + clazz
        + ", primaryKey="
        + primaryKey
        + ", fields="
        + fields
        + '}';
  }
}
