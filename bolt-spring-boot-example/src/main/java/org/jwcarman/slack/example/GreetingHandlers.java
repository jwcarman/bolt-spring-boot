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

import org.jwcarman.slack.bolt.autoconfigure.annotations.SlackController;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlashCommand;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.CommandText;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserId;
import org.jwcarman.slack.bolt.autoconfigure.annotations.bind.UserName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.slack.api.bolt.response.Response;

/**
 * Demonstrates slash command handlers with different return types and parameter injection.
 */
@SlackController
public class GreetingHandlers {

  private static final Logger log = LoggerFactory.getLogger(GreetingHandlers.class);

  /**
   * Returns a String — the framework wraps it in Response.ok(text) automatically.
   */
  @SlashCommand("/hello")
  public String hello(@UserName String name) {
    log.info("Received /hello from {}", name);
    return "Hello, " + name + "!";
  }

  /**
   * Returns void — the framework auto-acknowledges for you.
   */
  @SlashCommand("/log")
  public void logCommand(@UserId String userId, @CommandText String text) {
    log.info("User {} said: {}", userId, text);
  }

  /**
   * Returns a Response for full control over the response.
   */
  @SlashCommand("/echo")
  public Response echo(@CommandText String text) {
    return Response.ok(":mega: " + text);
  }
}
