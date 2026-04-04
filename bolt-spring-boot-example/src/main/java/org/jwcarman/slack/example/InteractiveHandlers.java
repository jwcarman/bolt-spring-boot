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

import static com.slack.api.model.block.Blocks.asBlocks;
import static com.slack.api.model.block.Blocks.input;
import static com.slack.api.model.block.composition.BlockCompositions.plainText;
import static com.slack.api.model.block.element.BlockElements.plainTextInput;
import static com.slack.api.model.view.Views.view;

import com.slack.api.model.view.ViewSubmit;
import com.slack.api.model.view.ViewTitle;

import org.jwcarman.slack.bolt.autoconfigure.annotations.BlockAction;
import org.jwcarman.slack.bolt.autoconfigure.annotations.GlobalShortcut;
import org.jwcarman.slack.bolt.autoconfigure.annotations.Message;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlackController;
import org.jwcarman.slack.bolt.autoconfigure.annotations.ViewSubmission;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.ActionValue;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.Block;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.MessageText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.TriggerId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.context.builtin.GlobalShortcutContext;
import com.slack.api.bolt.response.Response;

/**
 * Demonstrates block actions, global shortcuts, view submissions with @Block binding, and message
 * pattern matching.
 */
@SlackController
public class InteractiveHandlers {

  private static final Logger log = LoggerFactory.getLogger(InteractiveHandlers.class);

  /**
   * Handles a block action — demonstrates @ActionValue injection.
   */
  @BlockAction("feedback-rating")
  public void onFeedbackRating(@UserId String userId, @ActionValue String rating) {
    log.info("User {} rated: {}", userId, rating);
  }

  /**
   * Opens a modal from a global shortcut — demonstrates @TriggerId injection and raw context
   * access.
   */
  @GlobalShortcut("open-feedback-form")
  public Response openFeedbackForm(@TriggerId String triggerId, GlobalShortcutContext ctx)
      throws Exception {
    var response =
        ctx.client()
            .viewsOpen(
                r ->
                    r.triggerId(triggerId)
                        .view(
                            view(
                                v ->
                                    v.type("modal")
                                        .callbackId("submit-feedback")
                                        .title(ViewTitle.builder().type("plain_text").text("Feedback").build())
                                        .submit(ViewSubmit.builder().type("plain_text").text("Submit").build())
                                        .blocks(
                                            asBlocks(
                                                input(
                                                    i ->
                                                        i.blockId("feedback")
                                                            .label(plainText("Your Feedback"))
                                                            .element(
                                                                plainTextInput(
                                                                    t ->
                                                                        t.actionId("comments")
                                                                            .multiline(
                                                                                true)))))))));
    if (!response.isOk()) {
      log.error("Failed to open modal: {} - {}", response.getError(), response.getResponseMetadata());
    }
    return ctx.ack();
  }

  /**
   * Handles a view submission — demonstrates @Block record binding.
   */
  public record FeedbackForm(String comments) {}

  @ViewSubmission("submit-feedback")
  public void onFeedbackSubmit(@UserId String userId, @Block("feedback") FeedbackForm feedback) {
    log.info("Feedback from {}: {}", userId, feedback.comments());
  }

  /**
   * Matches messages containing "hello" (case-insensitive) — demonstrates @Message with
   * @MessageText.
   */
  @Message("(?i)hello")
  public void onHello(@MessageText String text, EventContext ctx) throws Exception {
    ctx.say("I heard you say: " + text);
  }

  /**
   * Returns an object — demonstrates JSON serialization return type.
   */
  @BlockAction("get-status")
  public StatusResponse getStatus(@UserId String userId) {
    return new StatusResponse("ok", userId);
  }

  public record StatusResponse(String status, String userId) {}
}
