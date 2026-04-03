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

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;

class MessageTextResolverTest {

  private final ParameterResolver resolver = MessageTextResolver.create();

  @Test
  void extractsFromMessageEvent() {
    var payload = mock(EventsApiPayload.class, Answers.RETURNS_DEEP_STUBS);
    MessageEvent event = new MessageEvent();
    event.setText("hello from message");
    when(payload.getEvent()).thenReturn(event);
    assertThat(resolver.resolve(payload, null)).isEqualTo("hello from message");
  }

  @Test
  void extractsFromAppMentionEvent() {
    var payload = mock(EventsApiPayload.class, Answers.RETURNS_DEEP_STUBS);
    AppMentionEvent event = new AppMentionEvent();
    event.setText("hey bot");
    when(payload.getEvent()).thenReturn(event);
    assertThat(resolver.resolve(payload, null)).isEqualTo("hey bot");
  }

  @Test
  @SuppressWarnings("DataFlowIssue")
  void throwsForUnsupportedRequest() {
    SlashCommandRequest req = mock(SlashCommandRequest.class);
    assertThatThrownBy(() -> resolver.resolve(req, null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
