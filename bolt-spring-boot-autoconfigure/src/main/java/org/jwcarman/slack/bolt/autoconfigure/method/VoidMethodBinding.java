package org.jwcarman.slack.bolt.autoconfigure.method;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBinding;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

public class VoidMethodBinding<R, C extends Context> extends AbstractMethodBinding<Void, R, C> {

  // --------------------------- CONSTRUCTORS ---------------------------

  public VoidMethodBinding(Object target, Method method, ParameterBinding[] parameterBindings) {
    super(target, method, Void.TYPE, parameterBindings);
  }

  // -------------------------- OTHER METHODS --------------------------

  @Override
  protected Response toResponse(C ctx, Void returnValue) {
    return ctx.ack();
  }
}
