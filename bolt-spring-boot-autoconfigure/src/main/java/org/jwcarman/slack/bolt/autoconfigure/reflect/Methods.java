package org.jwcarman.slack.bolt.autoconfigure.reflect;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.SlackHandlerInvocationException;

/** Utility methods for reflective method invocation. */
public class Methods {
  private Methods() {
    // Utility class
  }

  /**
   * Invokes the given method on the target bean and casts the result to the expected type.
   *
   * @param <R> the expected return type
   * @param responseType the expected return type class
   * @param bean the target object
   * @param method the method to invoke
   * @param args the method arguments
   * @return the method return value cast to the expected type
   * @throws SlackHandlerInvocationException if the method invocation fails
   */
  public static <R> R invoke(Class<R> responseType, Object bean, Method method, Object[] args) {
    try {
      return responseType.cast(method.invoke(bean, args));
    } catch (ReflectiveOperationException e) {
      throw new SlackHandlerInvocationException(
          String.format("Method invocation failed for method %s.", method), e);
    }
  }
}
