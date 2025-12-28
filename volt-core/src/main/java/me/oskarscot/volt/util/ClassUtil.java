package me.oskarscot.volt.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

public final class ClassUtil {

  private ClassUtil() {}

  public static boolean hasPublicNoArgConstructor(Class<?> clazz) {
    try {
      Constructor<?> constructor = clazz.getDeclaredConstructor();
      return Modifier.isPublic(constructor.getModifiers());
    } catch (NoSuchMethodException e) {
      return false;
    }
  }

  public static <T> T construct(Class<T> clazz) {
    T instance;
    try {
      instance = clazz.getDeclaredConstructor().newInstance();
    } catch (InstantiationException
        | IllegalAccessException
        | InvocationTargetException
        | NoSuchMethodException e) {
      throw new RuntimeException(e);
    }
    return instance;
  }

  public static Object getFieldValue(Field field, Object instance) {
    field.setAccessible(true);
    Object value = null;
    try {
      value = field.get(instance);
    } catch (IllegalAccessException e) {
      System.out.println(e.getMessage());
      ;
    }
    return value;
  }
}
