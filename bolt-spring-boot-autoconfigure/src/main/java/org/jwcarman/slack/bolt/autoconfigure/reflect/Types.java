package org.jwcarman.slack.bolt.autoconfigure.reflect;

import java.lang.reflect.RecordComponent;
import java.util.Map;

public class Types {

  /**
   * Creates a new instance of the given record type using its canonical constructor.
   *
   * @param recordType the record class to instantiate
   * @param components the record components (from {@code recordType.getRecordComponents()})
   * @param args the constructor arguments, matching the components in order
   * @return the new record instance
   * @throws IllegalArgumentException if the record cannot be constructed
   */
  /**
   * Creates a new instance of the given record type using its canonical constructor.
   *
   * @param recordType the record class to instantiate
   * @param args the constructor arguments, matching the record components in order
   * @return the new record instance
   * @throws IllegalArgumentException if the type is not a record or cannot be constructed
   */
  public static Object newRecord(Class<?> recordType, Object[] args) {
    try {
      RecordComponent[] components = recordType.getRecordComponents();
      Class<?>[] paramTypes = new Class[components.length];
      for (int i = 0; i < components.length; i++) {
        paramTypes[i] = components[i].getType();
      }
      return recordType.getDeclaredConstructor(paramTypes).newInstance(args);
    } catch (ReflectiveOperationException e) {
      throw new IllegalArgumentException("Failed to construct " + recordType.getSimpleName(), e);
    }
  }

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

  private Types() {
    // Utility class
  }
}
