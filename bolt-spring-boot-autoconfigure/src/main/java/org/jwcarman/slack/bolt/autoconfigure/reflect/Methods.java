package org.jwcarman.slack.bolt.autoconfigure.reflect;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.SlackHandlerInvocationException;

public class Methods {
  private Methods() {
    // Utility class
  }

  public static <R> R invoke(Class<R> responseType, Object bean, Method method, Object[] args) {
    try {
      return responseType.cast(method.invoke(bean, args));
    } catch (ReflectiveOperationException e) {
      throw new SlackHandlerInvocationException(
          String.format("Method invocation failed for method %s.", method), e);
    }
  }
}
