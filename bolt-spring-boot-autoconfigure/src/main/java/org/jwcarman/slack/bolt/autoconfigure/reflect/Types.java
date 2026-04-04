package org.jwcarman.slack.bolt.autoconfigure.reflect;

import java.util.Map;

public class Types {

  // ------------------------------ FIELDS ------------------------------

  private static final Map<Class<?>, Object> NULL_VALUES =
      Map.of(
          Integer.TYPE,
          0,
          Long.TYPE,
          0L,
          Short.TYPE,
          (short) 0,
          Byte.TYPE,
          (byte) 0,
          Double.TYPE,
          0.0,
          Float.TYPE,
          0.0f,
          Character.TYPE,
          '\0',
          Boolean.TYPE,
          false);

  // -------------------------- STATIC METHODS --------------------------

  public static Object nullValue(Class<?> type) {
    return NULL_VALUES.getOrDefault(type, null);
  }

  // --------------------------- CONSTRUCTORS ---------------------------

  private Types() {}
}
