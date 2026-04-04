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

import com.slack.api.bolt.request.builtin.AttachmentActionRequest;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.BlockSuggestionRequest;
import com.slack.api.bolt.request.builtin.DialogCancellationRequest;
import com.slack.api.bolt.request.builtin.DialogSubmissionRequest;
import com.slack.api.bolt.request.builtin.DialogSuggestionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewClosedRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;

public final class TeamIdParameterBinding implements ParameterBinding {

  @Override
  public String resolve(Object request, Object context) {
    return switch (request) {
      case SlashCommandRequest r -> r.getPayload().getTeamId();
      case BlockActionRequest r -> r.getPayload().getTeam().getId();
      case ViewSubmissionRequest r -> r.getPayload().getTeam().getId();
      case GlobalShortcutRequest r -> r.getPayload().getTeam().getId();
      case MessageShortcutRequest r -> r.getPayload().getTeam().getId();
      case DialogSubmissionRequest r -> r.getPayload().getTeam().getId();
      case DialogSuggestionRequest r -> r.getPayload().getTeam().getId();
      case DialogCancellationRequest r -> r.getPayload().getTeam().getId();
      case AttachmentActionRequest r -> r.getPayload().getTeam().getId();
      case BlockSuggestionRequest r -> r.getPayload().getTeam().getId();
      case ViewClosedRequest r -> r.getPayload().getTeam().getId();
      default ->
          throw new IllegalArgumentException(
              "@TeamId not supported for " + request.getClass().getSimpleName());
    };
  }
}
