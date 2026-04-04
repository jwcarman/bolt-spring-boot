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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.Answers;

import com.slack.api.bolt.request.builtin.AttachmentActionRequest;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.BlockSuggestionRequest;
import com.slack.api.bolt.request.builtin.DialogCancellationRequest;
import com.slack.api.bolt.request.builtin.DialogSubmissionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewClosedRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;

class TeamIdParameterBindingTest {

  private final ParameterBinding binding = new TeamIdParameterBinding();

  @Test
  void extractsFromSlashCommand() {
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeamId()).thenReturn("T12345");
    assertThat(binding.resolve(req, null)).isEqualTo("T12345");
  }

  @Test
  void extractsFromBlockAction() {
    var req = mock(BlockActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeam().getId()).thenReturn("T67890");
    assertThat(binding.resolve(req, null)).isEqualTo("T67890");
  }

  @Test
  void extractsFromViewSubmission() {
    var req = mock(ViewSubmissionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeam().getId()).thenReturn("T11111");
    assertThat(binding.resolve(req, null)).isEqualTo("T11111");
  }

  @Test
  void extractsFromGlobalShortcut() {
    var req = mock(GlobalShortcutRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeam().getId()).thenReturn("T22222");
    assertThat(binding.resolve(req, null)).isEqualTo("T22222");
  }

  @Test
  void extractsFromMessageShortcut() {
    var req = mock(MessageShortcutRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeam().getId()).thenReturn("T33333");
    assertThat(binding.resolve(req, null)).isEqualTo("T33333");
  }

  @Test
  void extractsFromDialogSubmission() {
    var req = mock(DialogSubmissionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeam().getId()).thenReturn("T44444");
    assertThat(binding.resolve(req, null)).isEqualTo("T44444");
  }

  @Test
  void extractsFromDialogCancellation() {
    var req = mock(DialogCancellationRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeam().getId()).thenReturn("T55555");
    assertThat(binding.resolve(req, null)).isEqualTo("T55555");
  }

  @Test
  void extractsFromAttachmentAction() {
    var req = mock(AttachmentActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeam().getId()).thenReturn("T66666");
    assertThat(binding.resolve(req, null)).isEqualTo("T66666");
  }

  @Test
  void extractsFromBlockSuggestion() {
    var req = mock(BlockSuggestionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeam().getId()).thenReturn("T77777");
    assertThat(binding.resolve(req, null)).isEqualTo("T77777");
  }

  @Test
  void extractsFromViewClosed() {
    var req = mock(ViewClosedRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTeam().getId()).thenReturn("T88888");
    assertThat(binding.resolve(req, null)).isEqualTo("T88888");
  }

  @Test
  void throwsForUnsupportedRequest() {
    assertThatThrownBy(() -> binding.resolve("not a request", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
