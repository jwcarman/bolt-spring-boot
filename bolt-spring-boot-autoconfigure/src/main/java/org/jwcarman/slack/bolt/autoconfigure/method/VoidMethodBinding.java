package org.jwcarman.slack.bolt.autoconfigure.method;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBinding;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

/**
 * Method binding for void handlers that auto-acknowledge.
 *
 * @param <R> the request type
 * @param <C> the context type
 */
public class VoidMethodBinding<R, C extends Context> extends AbstractMethodBinding<Void, R, C> {

  // --------------------------- CONSTRUCTORS ---------------------------

  /**
   * Creates a new void method binding.
   *
   * @param target the handler bean instance
   * @param method the handler method
   * @param parameterBindings the parameter bindings for the method
   */
  public VoidMethodBinding(Object target, Method method, ParameterBinding[] parameterBindings) {
    super(target, method, Void.TYPE, parameterBindings);
  }

  // -------------------------- OTHER METHODS --------------------------

  @Override
  protected Response toResponse(C ctx, Void returnValue) {
    return ctx.ack();
  }
}
