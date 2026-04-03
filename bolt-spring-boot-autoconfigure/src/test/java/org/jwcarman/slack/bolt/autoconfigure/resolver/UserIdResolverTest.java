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

import static org.assertj.core.api.Assertions.assertThat;
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
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewClosedRequest;

class UserIdResolverTest {

  private final ParameterResolver resolver = UserIdResolver.create();

  @Test
  void extractsFromSlashCommand() {
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUserId()).thenReturn("U12345");
    assertThat(resolver.resolve(req, null)).isEqualTo("U12345");
  }

  @Test
  void extractsFromBlockAction() {
    var req = mock(BlockActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getId()).thenReturn("U67890");
    assertThat(resolver.resolve(req, null)).isEqualTo("U67890");
  }

  @Test
  void extractsFromDialogSubmission() {
    var req = mock(DialogSubmissionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getId()).thenReturn("U11111");
    assertThat(resolver.resolve(req, null)).isEqualTo("U11111");
  }

  @Test
  void extractsFromDialogSuggestion() {
    var req = mock(DialogSuggestionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getId()).thenReturn("U22222");
    assertThat(resolver.resolve(req, null)).isEqualTo("U22222");
  }

  @Test
  void extractsFromDialogCancellation() {
    var req = mock(DialogCancellationRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getId()).thenReturn("U33333");
    assertThat(resolver.resolve(req, null)).isEqualTo("U33333");
  }

  @Test
  void extractsFromAttachmentAction() {
    var req = mock(AttachmentActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getId()).thenReturn("U44444");
    assertThat(resolver.resolve(req, null)).isEqualTo("U44444");
  }

  @Test
  void extractsFromBlockSuggestion() {
    var req = mock(BlockSuggestionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getId()).thenReturn("U55555");
    assertThat(resolver.resolve(req, null)).isEqualTo("U55555");
  }

  @Test
  void extractsFromViewClosed() {
    var req = mock(ViewClosedRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getUser().getId()).thenReturn("U66666");
    assertThat(resolver.resolve(req, null)).isEqualTo("U66666");
  }
}
