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
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import java.lang.reflect.Parameter;

import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ActionValue;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.Block;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ChannelId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.CommandText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.MessageText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ResponseUrl;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TeamId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TriggerId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserName;
import org.springframework.core.convert.ConversionService;

/**
 * Factory that inspects a {@link Parameter} and returns the appropriate {@link ParameterResolver}.
 *
 * <p>Resolution order:
 *
 * <ol>
 *   <li>Binding annotations ({@code @UserId}, {@code @UserName}, etc.)
 *   <li>{@code @Block} annotation for view submission record binding
 *   <li>Parameter type matching the handler's request type
 *   <li>Parameter type matching the handler's context type
 *   <li>No match throws {@link IllegalArgumentException}
 * </ol>
 */
public class ParameterResolverFactory {

  /**
   * Creates a {@link ParameterResolver} for the given method parameter based on its annotations and
   * type.
   *
   * @param parameter the method parameter to resolve
   * @param requestType the Bolt handler's expected request type
   * @param contextType the Bolt handler's expected context type
   * @param conversionService the conversion service for type coercion
   * @return a parameter resolver for the given parameter
   * @throws IllegalArgumentException if the parameter cannot be resolved
   */
  public static ParameterResolver createResolver(
      Parameter parameter,
      Class<?> requestType,
      Class<?> contextType,
      ConversionService conversionService) {

    // 1. Check for binding annotations
    if (parameter.isAnnotationPresent(UserId.class)) {
      return UserIdResolver.create();
    }
    if (parameter.isAnnotationPresent(UserName.class)) {
      return UserNameResolver.create();
    }
    if (parameter.isAnnotationPresent(TeamId.class)) {
      return TeamIdResolver.create();
    }
    if (parameter.isAnnotationPresent(ChannelId.class)) {
      return ChannelIdResolver.create();
    }
    if (parameter.isAnnotationPresent(TriggerId.class)) {
      return TriggerIdResolver.create();
    }
    if (parameter.isAnnotationPresent(ResponseUrl.class)) {
      return ResponseUrlResolver.create();
    }
    if (parameter.isAnnotationPresent(CommandText.class)) {
      return CommandTextResolver.create();
    }
    if (parameter.isAnnotationPresent(ActionValue.class)) {
      return ActionValueResolver.create();
    }
    if (parameter.isAnnotationPresent(MessageText.class)) {
      return MessageTextResolver.create();
    }

    // 2. Check for @Block annotation
    if (parameter.isAnnotationPresent(Block.class)) {
      Block block = parameter.getAnnotation(Block.class);
      String blockName = block.value().isEmpty() ? parameter.getName() : block.value();
      return BlockParameterResolver.create(blockName, parameter.getType(), conversionService);
    }

    // 3. Check if parameter type matches the handler's request type
    if (parameter.getType().isAssignableFrom(requestType)) {
      return (req, ctx) -> req;
    }

    // 4. Check if parameter type matches the handler's context type
    if (parameter.getType().isAssignableFrom(contextType)) {
      return (req, ctx) -> ctx;
    }

    // 5. No match — fail fast
    throw new IllegalArgumentException(
        "Cannot resolve parameter '"
            + parameter.getName()
            + "' of type "
            + parameter.getType().getSimpleName()
            + " in handler method. Annotate it with a binding annotation or use a supported"
            + " request/context type.");
  }
}
