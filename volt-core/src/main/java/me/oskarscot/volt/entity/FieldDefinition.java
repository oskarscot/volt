package me.oskarscot.volt.entity;

import java.lang.reflect.Field;
import org.jetbrains.annotations.ApiStatus.Internal;

public class FieldDefinition {

  private final String columnName;
  private final Field field;

  @Internal
  FieldDefinition(String columnName, Field field) {
    this.columnName = columnName;
    this.field = field;
    this.field.setAccessible(true);
  }

  public Field getField() {
    return field;
  }

  public String getColumnName() {
    return columnName;
  }

  @Override
  public String toString() {
    return "FieldDefinition{" + ", name='" + columnName + '\'' + '}';
  }
}
