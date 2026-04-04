/*
 * Copyright © 2026 James Carman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jwcarman.slack.bolt.autoconfigure.method;

import java.lang.reflect.Method;

import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBindingFactory;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

/**
 * Factory that creates {@link MethodBinding} instances by delegating parameter resolution to a
 * {@link ParameterBindingFactory} and selecting the appropriate binding implementation based on the
 * method's return type.
 *
 * @see MethodBinding
 * @see ParameterBindingFactory
 */
public class MethodBindingFactory {

  private final ParameterBindingFactory parameterBindingFactory;

  /**
   * Creates a new factory with the given parameter binding factory.
   *
   * @param parameterBindingFactory the factory used to create parameter bindings
   */
  public MethodBindingFactory(ParameterBindingFactory parameterBindingFactory) {
    this.parameterBindingFactory = parameterBindingFactory;
  }

  /**
   * Creates a {@link MethodBinding} for the given bean method, choosing the correct implementation
   * based on the method's return type ({@link Response}, {@link String}, {@code void}, or JSON
   * fallback).
   *
   * @param <R> the request type
   * @param <C> the context type
   * @param bean the target bean instance
   * @param method the handler method
   * @param requestType the expected request class
   * @param contextType the expected context class
   * @return a method binding that can invoke the handler
   */
  public <R, C extends Context> MethodBinding<R, C> create(
      Object bean, Method method, Class<R> requestType, Class<C> contextType) {
    var parameterBindings =
        parameterBindingFactory.createBindings(method, requestType, contextType);
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
}
