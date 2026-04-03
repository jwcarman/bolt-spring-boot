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
import org.mockito.Answers;

import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;

class ChannelIdResolverTest {

  private final ParameterResolver resolver = ChannelIdResolver.create();

  @Test
  void extractsFromSlashCommand() {
    var req = mock(SlashCommandRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannelId()).thenReturn("C12345");
    assertThat(resolver.resolve(req, null)).isEqualTo("C12345");
  }

  @Test
  void extractsFromBlockAction() {
    var req = mock(BlockActionRequest.class, Answers.RETURNS_DEEP_STUBS);
    when(req.getPayload().getChannel().getId()).thenReturn("C67890");
    assertThat(resolver.resolve(req, null)).isEqualTo("C67890");
  }

  @Test
  @SuppressWarnings("DataFlowIssue")
  void throwsForUnsupportedRequest() {
    GlobalShortcutRequest req = mock(GlobalShortcutRequest.class);
    assertThatThrownBy(() -> resolver.resolve(req, null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
