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
package org.jwcarman.slack.bolt.autoconfigure.parameter;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Optional;

import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ActionValue;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ChannelId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.CommandText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.MessageText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ResponseUrl;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TeamId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TriggerId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserName;
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

  static ParameterBinding nullSafe(Parameter parameter, ParameterBinding original) {
    var nullValue = Types.nullValue(parameter.getType());
    return (request, ctx) -> Optional.ofNullable(original.resolve(request, ctx)).orElse(nullValue);
  }

  private static <R, C extends Context> ParameterBinding resolve(
      Parameter parameter, Class<R> requestType, Class<C> contextType) {
    if (parameter.isAnnotationPresent(UserId.class)) {
      return new UserIdParameterBinding();
    }
    if (parameter.isAnnotationPresent(UserName.class)) {
      return new UserNameParameterBinding();
    }
    if (parameter.isAnnotationPresent(TeamId.class)) {
      return new TeamIdParameterBinding();
    }
    if (parameter.isAnnotationPresent(ChannelId.class)) {
      return new ChannelIdParameterBinding();
    }
    if (parameter.isAnnotationPresent(TriggerId.class)) {
      return new TriggerIdParameterBinding();
    }
    if (parameter.isAnnotationPresent(ResponseUrl.class)) {
      return new ResponseUrlParameterBinding();
    }
    if (parameter.isAnnotationPresent(CommandText.class)) {
      return new CommandTextParameterBinding();
    }
    if (parameter.isAnnotationPresent(ActionValue.class)) {
      return new ActionValueParameterBinding();
    }
    if (parameter.isAnnotationPresent(MessageText.class)) {
      return new MessageTextParameterBinding();
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
