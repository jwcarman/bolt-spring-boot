package org.jwcarman.slack.bolt.autoconfigure.method;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBinding;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

/**
 * Method binding for handlers that return a String, wrapped in Response.ok().
 *
 * @param <R> the request type
 * @param <C> the context type
 */
public class StringMethodBinding<R, C extends Context> extends AbstractMethodBinding<String, R, C> {

  // --------------------------- CONSTRUCTORS ---------------------------

  /**
   * Creates a new string method binding.
   *
   * @param target the handler bean instance
   * @param method the handler method
   * @param parameterBindings the parameter bindings for the method
   */
  public StringMethodBinding(Object target, Method method, ParameterBinding[] parameterBindings) {
    super(target, method, String.class, parameterBindings);
  }

  // -------------------------- OTHER METHODS --------------------------

  @Override
  protected Response toResponse(C ctx, String returnValue) {
    return Response.ok(returnValue);
  }
}
