package com.kamegatze.map.result.set.context;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public final class Context {
  private static final Map<Class<?>, Object> values = new HashMap<>();

  private Context() {}

  public static void set(Object value) {
    values.put(value.getClass(), value);
  }

  @SuppressWarnings("unchecked")
  public static <T> T get(Class<T> clazz) {
    for (Map.Entry<Class<?>, Object> entry : values.entrySet()) {
      if (clazz.isAssignableFrom(entry.getKey())) {
        return (T) entry.getValue();
      }
    }
    throw new NoSuchElementException(
        "Class " + clazz.getName() + " not found in context by annotation processing");
  }
}
