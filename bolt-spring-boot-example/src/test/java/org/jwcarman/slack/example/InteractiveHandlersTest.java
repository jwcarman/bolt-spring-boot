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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.jwcarman.slack.example.InteractiveHandlers.FeedbackForm;
import org.jwcarman.slack.example.InteractiveHandlers.StatusResponse;

/**
 * Demonstrates unit testing interactive handlers. The @Block record binding means your handler
 * receives a simple record — no Slack API objects needed in tests.
 */
class InteractiveHandlersTest {

  private final InteractiveHandlers handlers = new InteractiveHandlers();

  @Test
  void onFeedbackRatingDoesNotThrow() {
    // void handler with @UserId and @ActionValue — just call with values
    handlers.onFeedbackRating("U123", "5");
  }

  @Test
  void onFeedbackSubmitDoesNotThrow() {
    // @Block record binding — test with a plain record, no view state needed
    var feedback = new FeedbackForm("Great framework!");
    handlers.onFeedbackSubmit("U456", feedback);
  }

  @Test
  void getStatusReturnsResponse() {
    // JSON return type — returns a record
    StatusResponse response = handlers.getStatus("U789");
    assertThat(response.status()).isEqualTo("ok");
    assertThat(response.userId()).isEqualTo("U789");
  }
}
