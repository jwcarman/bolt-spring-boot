package org.jwcarman.slack.bolt.autoconfigure.parameter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.CommandText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserId;
import org.jwcarman.slack.bolt.autoconfigure.reflect.Types;

import com.slack.api.bolt.context.Context;

public class ParameterBindings {

  public static final ParameterBinding[] EMPTY_BINDINGS = new ParameterBinding[0];
  public static final Object[] EMPTY_PARAMETERS = new Object[0];

  private ParameterBindings() {}

  public static Object[] resolve(
      ParameterBinding[] parameterBindings, Object request, Object context) {
    if (parameterBindings.length == 0) {
      return EMPTY_PARAMETERS;
    }
    var parameters = new Object[parameterBindings.length];
    for (int i = 0; i < parameterBindings.length; i++) {
      parameters[i] = parameterBindings[i].resolve(request, context);
    }
    return parameters;
  }

  public static <R, C extends Context> ParameterBinding[] resolve(
      Method method, Class<R> requestType, Class<C> contextType) {
    var parameterCount = method.getParameterCount();
    if (parameterCount == 0) {
      return EMPTY_BINDINGS;
    }
    var parameters = method.getParameters();
    var bindings = new ParameterBinding[parameterCount];
    for (int i = 0; i < parameters.length; i++) {
      var parameter = parameters[i];
      bindings[i] = nullSafe(parameter, resolve(parameter, requestType, contextType));
    }
    return bindings;
  }

  private static ParameterBinding nullSafe(Parameter parameter, ParameterBinding original) {
    var nullValue = Types.nullValue(parameter.getType());
    return (request, ctx) -> Optional.ofNullable(original.resolve(request, ctx)).orElse(nullValue);
  }

  private static <R, C extends Context> ParameterBinding resolve(
      Parameter parameter, Class<R> requestType, Class<C> contextType) {
    if (parameter.isAnnotationPresent(UserId.class)) {
      return new UserIdParameterBinding();
    }
    if (parameter.isAnnotationPresent(CommandText.class)) {
      return new CommandTextParameterBinding();
    }
    if (requestType.isAssignableFrom(parameter.getType())) {
      return (request, ctx) -> request;
    }
    if (contextType.isAssignableFrom(parameter.getType())) {
      return (request, ctx) -> ctx;
    }
    throw new IllegalArgumentException("Unable to bind to parameter " + parameter);
  }
}
