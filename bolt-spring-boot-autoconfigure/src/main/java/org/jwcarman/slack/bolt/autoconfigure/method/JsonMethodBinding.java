package org.jwcarman.slack.bolt.autoconfigure.method;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBinding;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

/**
 * Method binding for handlers that return an object, serialized to JSON.
 *
 * @param <R> the request type
 * @param <C> the context type
 */
public class JsonMethodBinding<R, C extends Context> extends AbstractMethodBinding<Object, R, C> {

  // --------------------------- CONSTRUCTORS ---------------------------

  /**
   * Creates a new JSON method binding.
   *
   * @param target the handler bean instance
   * @param method the handler method
   * @param parameterBindings the parameter bindings for the method
   */
  public JsonMethodBinding(Object target, Method method, ParameterBinding[] parameterBindings) {
    super(target, method, Object.class, parameterBindings);
  }

  // -------------------------- OTHER METHODS --------------------------

  @Override
  protected Response toResponse(C ctx, Object returnValue) {
    return ctx.ack(ctx.toJson(returnValue));
  }
}
