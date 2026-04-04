package org.jwcarman.slack.bolt.autoconfigure.method;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBinding;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

public class StringMethodBinding<R, C extends Context> extends AbstractMethodBinding<String, R, C> {

  // --------------------------- CONSTRUCTORS ---------------------------

  public StringMethodBinding(Object target, Method method, ParameterBinding[] parameterBindings) {
    super(target, method, String.class, parameterBindings);
  }

  // -------------------------- OTHER METHODS --------------------------

  @Override
  protected Response toResponse(C ctx, String returnValue) {
    return Response.ok(returnValue);
  }
}
