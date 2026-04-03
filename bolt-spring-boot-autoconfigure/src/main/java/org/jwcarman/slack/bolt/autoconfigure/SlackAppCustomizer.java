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
package org.jwcarman.slack.bolt.autoconfigure;

import com.slack.api.bolt.App;

/**
 * Callback interface for customizing the Slack Bolt {@link App} during auto-configuration.
 * Implementations are auto-discovered from the application context and applied before the {@code
 * App} bean is fully initialized.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * @Bean
 * SlackAppCustomizer myCustomizer() {
 *     return app -> app.command("/legacy", (req, ctx) -> ctx.ack("OK"));
 * }
 * }</pre>
 *
 * @see SlackAutoConfiguration
 */
@FunctionalInterface
public interface SlackAppCustomizer {
  /**
   * Customize the given {@link App}.
   *
   * @param app the Slack Bolt application to customize
   */
  void customize(App app);
}
