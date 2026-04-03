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

import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;

public class UserIdResolver {

  public static ParameterResolver create() {
    return (req, ctx) ->
        switch (req) {
          case SlashCommandRequest r -> r.getPayload().getUserId();
          case BlockActionRequest r -> r.getPayload().getUser().getId();
          case ViewSubmissionRequest r -> r.getPayload().getUser().getId();
          case GlobalShortcutRequest r -> r.getPayload().getUser().getId();
          case MessageShortcutRequest r -> r.getPayload().getUser().getId();
          default ->
              throw new IllegalArgumentException(
                  "@UserId not supported for " + req.getClass().getSimpleName());
        };
  }
}
