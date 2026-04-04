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
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;

class ChannelIdParameterBindingTest {

  private final ParameterBinding binding = new ChannelIdParameterBinding();

  @Test
  void extractsFromSlashCommand() {
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannelId()).thenReturn("C12345");
    assertThat(binding.resolve(req, null)).isEqualTo("C12345");
  }

  @Test
  void extractsFromBlockAction() {
    var req = mock(BlockActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannel().getId()).thenReturn("C67890");
    assertThat(binding.resolve(req, null)).isEqualTo("C67890");
  }

  @Test
  void extractsFromMessageShortcut() {
    var req = mock(MessageShortcutRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannel().getId()).thenReturn("C11111");
    assertThat(binding.resolve(req, null)).isEqualTo("C11111");
  }

  @Test
  void extractsFromDialogSubmission() {
    var req = mock(DialogSubmissionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannel().getId()).thenReturn("C22222");
    assertThat(binding.resolve(req, null)).isEqualTo("C22222");
  }

  @Test
  void extractsFromDialogSuggestion() {
    var req = mock(DialogSuggestionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannel().getId()).thenReturn("C33333");
    assertThat(binding.resolve(req, null)).isEqualTo("C33333");
  }

  @Test
  void extractsFromDialogCancellation() {
    var req = mock(DialogCancellationRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannel().getId()).thenReturn("C44444");
    assertThat(binding.resolve(req, null)).isEqualTo("C44444");
  }

  @Test
  void extractsFromAttachmentAction() {
    var req = mock(AttachmentActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannel().getId()).thenReturn("C55555");
    assertThat(binding.resolve(req, null)).isEqualTo("C55555");
  }

  @Test
  void extractsFromBlockSuggestion() {
    var req = mock(BlockSuggestionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannel().getId()).thenReturn("C66666");
    assertThat(binding.resolve(req, null)).isEqualTo("C66666");
  }

  @Test
  void throwsForUnsupportedRequest() {
    assertThatThrownBy(() -> binding.resolve("not a request", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
