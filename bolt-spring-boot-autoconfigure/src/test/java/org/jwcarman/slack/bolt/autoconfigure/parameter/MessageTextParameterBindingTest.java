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

import org.junit.jupiter.api.Test;

import com.slack.api.app_backend.events.payload.AppMentionPayload;
import com.slack.api.app_backend.events.payload.MessagePayload;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;

class MessageTextParameterBindingTest {

  private final ParameterBinding binding = new MessageTextParameterBinding();

  @Test
  void extractsFromMessageEvent() {
    MessageEvent event = new MessageEvent();
    event.setText("hello from message");
    MessagePayload payload = new MessagePayload();
    payload.setEvent(event);
    assertThat(binding.resolve(payload, null)).isEqualTo("hello from message");
  }

  @Test
  void extractsFromAppMentionEvent() {
    AppMentionEvent event = new AppMentionEvent();
    event.setText("hey bot");
    AppMentionPayload payload = new AppMentionPayload();
    payload.setEvent(event);
    assertThat(binding.resolve(payload, null)).isEqualTo("hey bot");
  }

  @Test
  @SuppressWarnings("DataFlowIssue")
  void throwsForUnsupportedRequest() {
    SlashCommandRequest req = mock(SlashCommandRequest.class);
    assertThatThrownBy(() -> binding.resolve(req, null))
        .isInstanceOf(IllegalArgumentException.class);
  }
}
