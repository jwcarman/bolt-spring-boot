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
package org.jwcarman.slack.example;

import org.jwcarman.slack.bolt.autoconfigure.annotations.Event;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlackController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;

/**
 * Demonstrates event handlers with the raw EventsApiPayload and EventContext.
 */
@SlackController
public class EventHandlers {

  private static final Logger log = LoggerFactory.getLogger(EventHandlers.class);

  /**
   * Responds to app mentions — demonstrates mixing raw payload with context.
   */
  @Event(AppMentionEvent.class)
  public void onMention(EventsApiPayload<AppMentionEvent> payload, EventContext ctx)
      throws Exception {
    var event = payload.getEvent();
    log.info("Mentioned by {} in {}: {}", event.getUser(), event.getChannel(), event.getText());
    ctx.say("You mentioned me! You said: " + event.getText());
    ctx.ack();
  }

  /**
   * Catch-all handler for message events that don't match a @Message pattern. Required because
   * subscribing to message.channels delivers all messages, and Bolt warns if there's no handler.
   */
  @Event(MessageEvent.class)
  public void onMessage() {
    // Intentionally empty — the @Message("(?i)hello") handler catches matching messages
  }
}
