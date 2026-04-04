package org.jwcarman.slack.bolt.autoconfigure.method;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBinding;
import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBindingFactory;
import org.jwcarman.slack.bolt.autoconfigure.reflect.Methods;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

/**
 * Base class for method bindings that handles parameter resolution and method invocation.
 *
 * @param <T> the handler method return type
 * @param <R> the request type
 * @param <C> the context type
 */
public abstract class AbstractMethodBinding<T, R, C extends Context>
    implements MethodBinding<R, C> {

  private final Object target;
  private final Method method;
  private final Class<T> returnType;
  private final ParameterBinding[] parameterBindings;

  AbstractMethodBinding(
      Object target, Method method, Class<T> returnType, ParameterBinding[] parameterBindings) {
    if (!returnType.isAssignableFrom(method.getReturnType())) {
      throw new IllegalArgumentException(
          String.format("Method %s does not return expected type %s", method, returnType));
    }
    this.target = target;
    this.method = method;
    this.returnType = returnType;
    this.parameterBindings = parameterBindings;
  }

  @Override
  public Response invoke(R request, C ctx) {
    var parameters = ParameterBindingFactory.resolve(parameterBindings, request, ctx);
    var returnValue = Methods.invoke(returnType, target, method, parameters);
    return toResponse(ctx, returnValue);
  }

  /**
   * Converts the handler method's return value to a Bolt Response.
   *
   * @param ctx the Bolt context
   * @param returnValue the value returned by the handler method
   * @return the Bolt response
   */
  protected abstract Response toResponse(C ctx, T returnValue);
}
