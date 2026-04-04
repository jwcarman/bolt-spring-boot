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

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;

/** Extracts the message text from an event payload. */
public final class MessageTextParameterBinding implements ParameterBinding {

  /** Creates a new {@code MessageTextParameterBinding}. */
  public MessageTextParameterBinding() {}

  @Override
  public String resolve(Object request, Object context) {
    return switch (request) {
      case EventsApiPayload<?> p -> {
        var event = p.getEvent();
        yield switch (event) {
          case MessageEvent m -> m.getText();
          case AppMentionEvent m -> m.getText();
          default ->
              throw new IllegalArgumentException(
                  "@MessageText not supported for event type " + event.getClass().getSimpleName());
        };
      }
      default ->
          throw new IllegalArgumentException(
              "@MessageText not supported for " + request.getClass().getSimpleName());
    };
  }
}
