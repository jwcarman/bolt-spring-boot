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
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;

class TriggerIdParameterBindingTest {

  private final ParameterBinding binding = new TriggerIdParameterBinding();

  @Test
  void extractsFromSlashCommand() {
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTriggerId()).thenReturn("tr12345");
    assertThat(binding.resolve(req, null)).isEqualTo("tr12345");
  }

  @Test
  void extractsFromBlockAction() {
    var req = mock(BlockActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTriggerId()).thenReturn("tr67890");
    assertThat(binding.resolve(req, null)).isEqualTo("tr67890");
  }

  @Test
  void extractsFromViewSubmission() {
    var req = mock(ViewSubmissionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTriggerId()).thenReturn("tr11111");
    assertThat(binding.resolve(req, null)).isEqualTo("tr11111");
  }

  @Test
  void extractsFromGlobalShortcut() {
    var req = mock(GlobalShortcutRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTriggerId()).thenReturn("tr22222");
    assertThat(binding.resolve(req, null)).isEqualTo("tr22222");
  }

  @Test
  void extractsFromMessageShortcut() {
    var req = mock(MessageShortcutRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTriggerId()).thenReturn("tr33333");
    assertThat(binding.resolve(req, null)).isEqualTo("tr33333");
  }

  @Test
  void extractsFromAttachmentAction() {
    var req = mock(AttachmentActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getTriggerId()).thenReturn("tr44444");
    assertThat(binding.resolve(req, null)).isEqualTo("tr44444");
  }

  @Test
  void throwsForUnsupportedRequest() {
    assertThatThrownBy(() -> binding.resolve("not a request", null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
