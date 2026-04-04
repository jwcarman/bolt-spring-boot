package org.jwcarman.slack.bolt.autoconfigure.method;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBindings;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

public class MethodBindings {

  public static <R, C extends Context> MethodBinding<R, C> create(
      Object bean, Method method, Class<R> requestType, Class<C> contextType) {
    var parameterBindings = ParameterBindings.resolve(method, requestType, contextType);
    var returnType = method.getReturnType();
    if (Response.class.equals(returnType)) {
      return new ResponseMethodBinding<>(bean, method, parameterBindings);
    }
    if (String.class.equals(returnType)) {
      return new StringMethodBinding<>(bean, method, parameterBindings);
    }
    if (Void.TYPE.equals(returnType)) {
      return new VoidMethodBinding<>(bean, method, parameterBindings);
    }
    return new JsonMethodBinding<>(bean, method, parameterBindings);
  }

  private MethodBindings() {
    // Utility class
  }
}
