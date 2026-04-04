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

import com.slack.api.bolt.request.builtin.SlashCommandRequest;

/** Extracts the command text from a slash command request. */
public final class CommandTextParameterBinding implements ParameterBinding {

  /** Creates a new {@code CommandTextParameterBinding}. */
  public CommandTextParameterBinding() {}

  @Override
  public Object resolve(Object request, Object context) {
    return switch (request) {
      case SlashCommandRequest r -> r.getPayload().getText();
      default ->
          throw new IllegalArgumentException(
              "@CommandText not supported for " + request.getClass().getSimpleName());
    };
  }
}
