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

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.app_backend.views.payload.ViewSubmissionPayload;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;

class TriggerIdResolverTest {

  private final ParameterResolver resolver = TriggerIdResolver.create();

  @Test
  void extractsFromSlashCommand() {
    SlashCommandRequest req = mock(SlashCommandRequest.class);
    SlashCommandPayload payload = mock(SlashCommandPayload.class);
    when(req.getPayload()).thenReturn(payload);
    when(payload.getTriggerId()).thenReturn("TR12345");
    assertThat(resolver.resolve(req, null)).isEqualTo("TR12345");
  }

  @Test
  void extractsFromViewSubmission() {
    ViewSubmissionRequest req = mock(ViewSubmissionRequest.class);
    ViewSubmissionPayload payload = mock(ViewSubmissionPayload.class);
    when(req.getPayload()).thenReturn(payload);
    when(payload.getTriggerId()).thenReturn("TR67890");
    assertThat(resolver.resolve(req, null)).isEqualTo("TR67890");
  }
}
