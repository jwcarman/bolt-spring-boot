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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;

class CommandTextResolverTest {

  private final ParameterResolver resolver = CommandTextResolver.create();

  @Test
  void extractsFromSlashCommand() {
    SlashCommandRequest req = mock(SlashCommandRequest.class);
    SlashCommandPayload payload = mock(SlashCommandPayload.class);
    when(req.getPayload()).thenReturn(payload);
    when(payload.getText()).thenReturn("hello world");
    assertThat(resolver.resolve(req, null)).isEqualTo("hello world");
  }

  @Test
  @SuppressWarnings("DataFlowIssue")
  void throwsForUnsupportedRequest() {
    BlockActionRequest req = mock(BlockActionRequest.class);
    assertThatThrownBy(() -> resolver.resolve(req, null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
