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
import com.slack.api.bolt.request.builtin.DialogSuggestionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewClosedRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;

class UserNameParameterBindingTest {

  private final ParameterBinding binding = new UserNameParameterBinding();

  @Test
  void extractsFromSlashCommand() {
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUserName()).thenReturn("jsmith");
    assertThat(binding.resolve(req, null)).isEqualTo("jsmith");
  }

  @Test
  void extractsFromBlockAction() {
    var req = mock(BlockActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getUsername()).thenReturn("jdoe");
    assertThat(binding.resolve(req, null)).isEqualTo("jdoe");
  }

  @Test
  void extractsFromDialogSubmission() {
    var req = mock(DialogSubmissionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getName()).thenReturn("alice");
    assertThat(binding.resolve(req, null)).isEqualTo("alice");
  }

  @Test
  void extractsFromDialogSuggestion() {
    var req = mock(DialogSuggestionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getName()).thenReturn("bob");
    assertThat(binding.resolve(req, null)).isEqualTo("bob");
  }

  @Test
  void extractsFromDialogCancellation() {
    var req = mock(DialogCancellationRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getName()).thenReturn("carol");
    assertThat(binding.resolve(req, null)).isEqualTo("carol");
  }

  @Test
  void extractsFromAttachmentAction() {
    var req = mock(AttachmentActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getName()).thenReturn("dave");
    assertThat(binding.resolve(req, null)).isEqualTo("dave");
  }

  @Test
  void extractsFromBlockSuggestion() {
    var req = mock(BlockSuggestionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getName()).thenReturn("eve");
    assertThat(binding.resolve(req, null)).isEqualTo("eve");
  }

  @Test
  void extractsFromViewSubmission() {
    var req = mock(ViewSubmissionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getName()).thenReturn("frank");
    assertThat(binding.resolve(req, null)).isEqualTo("frank");
  }

  @Test
  void extractsFromViewClosed() {
    var req = mock(ViewClosedRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getName()).thenReturn("grace");
    assertThat(binding.resolve(req, null)).isEqualTo("grace");
  }

  @Test
  void extractsFromGlobalShortcut() {
    var req = mock(GlobalShortcutRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getUsername()).thenReturn("heidi");
    assertThat(binding.resolve(req, null)).isEqualTo("heidi");
  }

  @Test
  void extractsFromMessageShortcut() {
    var req = mock(MessageShortcutRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getName()).thenReturn("ivan");
    assertThat(binding.resolve(req, null)).isEqualTo("ivan");
  }

  @Test
  void throwsForUnsupportedRequest() {
    assertThatThrownBy(() -> binding.resolve("not a request", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
