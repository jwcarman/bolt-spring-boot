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

import com.slack.api.bolt.request.builtin.AttachmentActionRequest;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;

/** Resolves the Slack trigger ID from supported request types. */
public class TriggerIdResolver {

  /**
   * Creates a {@link ParameterResolver} that extracts the trigger ID from the request.
   *
   * @return a parameter resolver for trigger ID extraction
   */
  public static ParameterResolver create() {
    return (req, ctx) ->
        switch (req) {
          case SlashCommandRequest r -> r.getPayload().getTriggerId();
          case BlockActionRequest r -> r.getPayload().getTriggerId();
          case ViewSubmissionRequest r -> r.getPayload().getTriggerId();
          case GlobalShortcutRequest r -> r.getPayload().getTriggerId();
          case MessageShortcutRequest r -> r.getPayload().getTriggerId();
          case AttachmentActionRequest r -> r.getPayload().getTriggerId();
          default ->
              throw new IllegalArgumentException(
                  "@TriggerId not supported for " + req.getClass().getSimpleName());
        };
  }
}
