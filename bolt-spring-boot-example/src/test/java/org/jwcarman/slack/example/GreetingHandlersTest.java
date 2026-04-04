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

/**
 * Demonstrates how easy it is to unit test handler methods. Because parameter injection lets you
 * declare simple types (String, etc.), your handlers are plain methods that need no Slack
 * infrastructure to test.
 */
class GreetingHandlersTest {

  private final GreetingHandlers handlers = new GreetingHandlers();

  @Test
  void helloReturnsGreeting() {
    var result = handlers.hello("James");
    assertThat(result).isEqualTo("Hello, James!");
  }

  @Test
  void echoReturnsResponseWithText() {
    var response = handlers.echo("test message");
    assertThat(response.getStatusCode()).isEqualTo(200);
    assertThat(response.getBody()).contains("test message");
  }

  @Test
  void logDoesNotThrow() {
    // void handler — just verify it doesn't throw
    handlers.logCommand("U123", "some text");
  }
}
