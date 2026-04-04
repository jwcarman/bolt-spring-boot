package org.jwcarman.slack.bolt.autoconfigure.method;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBinding;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

/**
 * Method binding for handlers that return Response directly.
 *
 * @param <R> the request type
 * @param <C> the context type
 */
public final class ResponseMethodBinding<R, C extends Context>
    extends AbstractMethodBinding<Response, R, C> {

  // --------------------------- CONSTRUCTORS ---------------------------

  /**
   * Creates a new response method binding.
   *
   * @param target the handler bean instance
   * @param method the handler method
   * @param parameterBindings the parameter bindings for the method
   */
  public ResponseMethodBinding(Object target, Method method, ParameterBinding[] parameterBindings) {
    super(target, method, Response.class, parameterBindings);
  }

  // -------------------------- OTHER METHODS --------------------------

  @Override
  protected Response toResponse(C ctx, Response returnValue) {
    return returnValue;
  }
}
