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
