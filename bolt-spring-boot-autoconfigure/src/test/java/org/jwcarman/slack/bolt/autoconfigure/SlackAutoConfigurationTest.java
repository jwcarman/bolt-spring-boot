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

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;

class SlackAutoConfigurationTest {

  private final WebApplicationContextRunner contextRunner =
      new WebApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(SlackAutoConfiguration.class))
          .withPropertyValues(
              "slack.client-id=test-client-id",
              "slack.client-secret=test-client-secret",
              "slack.signing-secret=test-signing-secret",
              "slack.scope=chat:write");

  @Test
  void createsAppConfigBean() {
    contextRunner.run(
        context -> {
          assertThat(context).hasSingleBean(AppConfig.class);
          AppConfig config = context.getBean(AppConfig.class);
          assertThat(config.getClientId()).isEqualTo("test-client-id");
          assertThat(config.getClientSecret()).isEqualTo("test-client-secret");
          assertThat(config.getSigningSecret()).isEqualTo("test-signing-secret");
          assertThat(config.getScope()).isEqualTo("chat:write");
        });
  }

  @Test
  void createsAppBean() {
    contextRunner.run(context -> assertThat(context).hasSingleBean(App.class));
  }

  @Test
  void appliesCustomizers() {
    contextRunner
        .withUserConfiguration(CustomizerConfig.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(App.class);
              assertThat(CustomizerConfig.customized).isTrue();
            });
  }

  @Test
  void registersEventServlet() {
    contextRunner.run(context -> assertThat(context).hasBean("slackEventsServlet"));
  }

  @Test
  void registersOAuthServlet() {
    contextRunner.run(context -> assertThat(context).hasBean("slackOAuthServlet"));
  }

  @Test
  void doesNotCreateBeansWithoutRequiredProperties() {
    new WebApplicationContextRunner()
        .withConfiguration(AutoConfigurations.of(SlackAutoConfiguration.class))
        .run(context -> assertThat(context).doesNotHaveBean(App.class));
  }

  static class CustomizerConfig {
    static boolean customized = false;

    @org.springframework.context.annotation.Bean
    SlackAppCustomizer testCustomizer() {
      return app -> customized = true;
    }
  }
}
