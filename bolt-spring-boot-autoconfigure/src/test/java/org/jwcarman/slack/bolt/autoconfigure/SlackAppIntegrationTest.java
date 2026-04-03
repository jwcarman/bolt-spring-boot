package org.jwcarman.slack.bolt.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlackController;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlashCommand;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.context.annotation.Bean;

import com.slack.api.bolt.App;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.response.Response;

class SlackAppIntegrationTest {

  private final WebApplicationContextRunner contextRunner =
      new WebApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(SlackAutoConfiguration.class))
          .withPropertyValues(
              "slack.client-id=test-client-id",
              "slack.client-secret=test-client-secret",
              "slack.signing-secret=test-signing-secret",
              "slack.scope=chat:write,commands");

  @Test
  void fullAutoConfigurationWithAnnotatedHandlers() {
    contextRunner
        .withUserConfiguration(TestHandlers.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(App.class);
              assertThat(context).hasSingleBean(SlackProperties.class);
              assertThat(context).hasBean("slackEventsServlet");
              assertThat(context).hasBean("slackOAuthServlet");
              assertThat(context).hasBean("annotationDrivenAppCustomizer");
            });
  }

  @Test
  void customServletPaths() {
    contextRunner
        .withPropertyValues(
            "slack.events-path=/custom/events",
            "slack.oauth-install-path=/custom/install",
            "slack.oauth-redirect-uri-path=/custom/redirect")
        .run(
            context -> {
              assertThat(context).hasSingleBean(App.class);
              SlackProperties props = context.getBean(SlackProperties.class);
              assertThat(props.getEventsPath()).isEqualTo("/custom/events");
              assertThat(props.getOauthInstallPath()).isEqualTo("/custom/install");
              assertThat(props.getOauthRedirectUriPath()).isEqualTo("/custom/redirect");
            });
  }

  @Test
  void programmaticCustomizerWorksAlongsideAnnotations() {
    contextRunner
        .withUserConfiguration(TestHandlers.class, ProgrammaticCustomizerConfig.class)
        .run(
            context -> {
              assertThat(context).hasSingleBean(App.class);
              assertThat(ProgrammaticCustomizerConfig.customized).isTrue();
            });
  }

  @SlackController
  static class TestHandlers {
    @SlashCommand("/hello")
    public Response hello(SlashCommandRequest req, SlashCommandContext ctx) {
      return ctx.ack("Hello!");
    }
  }

  static class ProgrammaticCustomizerConfig {
    static boolean customized = false;

    @Bean
    SlackAppCustomizer testCustomizer() {
      return app -> customized = true;
    }
  }
}
