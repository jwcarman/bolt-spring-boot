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
import com.slack.api.bolt.request.builtin.BlockSuggestionRequest;
import com.slack.api.bolt.request.builtin.DialogCancellationRequest;
import com.slack.api.bolt.request.builtin.DialogSubmissionRequest;
import com.slack.api.bolt.request.builtin.DialogSuggestionRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;

/** Resolves the Slack channel ID from supported request types. */
public class ChannelIdResolver {

  /**
   * Creates a {@link ParameterResolver} that extracts the channel ID from the request.
   *
   * @return a parameter resolver for channel ID extraction
   */
  public static ParameterResolver create() {
    return (req, ctx) ->
        switch (req) {
          case SlashCommandRequest r -> r.getPayload().getChannelId();
          case BlockActionRequest r -> r.getPayload().getChannel().getId();
          case MessageShortcutRequest r -> r.getPayload().getChannel().getId();
          case DialogSubmissionRequest r -> r.getPayload().getChannel().getId();
          case DialogSuggestionRequest r -> r.getPayload().getChannel().getId();
          case DialogCancellationRequest r -> r.getPayload().getChannel().getId();
          case AttachmentActionRequest r -> r.getPayload().getChannel().getId();
          case BlockSuggestionRequest r -> r.getPayload().getChannel().getId();
          default ->
              throw new IllegalArgumentException(
                  "@ChannelId not supported for " + req.getClass().getSimpleName());
        };
  }
}
