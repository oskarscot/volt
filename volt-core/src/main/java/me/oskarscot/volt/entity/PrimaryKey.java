package me.oskarscot.volt.entity;

import java.lang.reflect.Field;
import org.jetbrains.annotations.ApiStatus.Internal;

public class PrimaryKey extends FieldDefinition {

  private final PrimaryKeyType primaryKeyType;
  private final boolean generated;

  @Internal
  PrimaryKey(String name, Field field, PrimaryKeyType primaryKeyType, boolean generated) {
    super(name, field);
    this.primaryKeyType = primaryKeyType;
    this.generated = generated;
  }

  public PrimaryKeyType getPrimaryKeyType() {
    return primaryKeyType;
  }

  public boolean isGenerated() {
    return generated;
  }
}
