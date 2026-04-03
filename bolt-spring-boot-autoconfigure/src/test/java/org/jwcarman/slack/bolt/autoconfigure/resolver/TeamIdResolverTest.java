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

import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;

class TeamIdResolverTest {

  private final ParameterResolver resolver = TeamIdResolver.create();

  @Test
  void extractsFromSlashCommand() {
    SlashCommandRequest req = mock(SlashCommandRequest.class);
    SlashCommandPayload payload = mock(SlashCommandPayload.class);
    when(req.getPayload()).thenReturn(payload);
    when(payload.getTeamId()).thenReturn("T12345");
    assertThat(resolver.resolve(req, null)).isEqualTo("T12345");
  }

  @Test
  void extractsFromBlockAction() {
    BlockActionRequest req = mock(BlockActionRequest.class);
    BlockActionPayload payload = mock(BlockActionPayload.class);
    BlockActionPayload.Team team = new BlockActionPayload.Team();
    team.setId("T67890");
    when(req.getPayload()).thenReturn(payload);
    when(payload.getTeam()).thenReturn(team);
    assertThat(resolver.resolve(req, null)).isEqualTo("T67890");
  }
}
