package org.jwcarman.slack.bolt.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest(classes = SlackPropertiesTest.Config.class)
@TestPropertySource(
    properties = {
      "slack.client-id=test-client-id",
      "slack.client-secret=test-client-secret",
      "slack.signing-secret=test-signing-secret",
      "slack.scope=chat:write,commands",
      "slack.user-scope=identity.basic",
      "slack.events-path=/custom/events",
      "slack.oauth-install-path=/custom/install",
      "slack.oauth-redirect-uri-path=/custom/redirect",
      "slack.oauth-completion-url=https://example.com/done",
      "slack.oauth-cancellation-url=https://example.com/cancel"
    })
class SlackPropertiesTest {

  @EnableConfigurationProperties(SlackProperties.class)
  static class Config {}

  @Autowired private SlackProperties properties;

  @Test
  void bindsAllProperties() {
    assertThat(properties.getClientId()).isEqualTo("test-client-id");
    assertThat(properties.getClientSecret()).isEqualTo("test-client-secret");
    assertThat(properties.getSigningSecret()).isEqualTo("test-signing-secret");
    assertThat(properties.getScope()).isEqualTo("chat:write,commands");
    assertThat(properties.getUserScope()).isEqualTo("identity.basic");
    assertThat(properties.getEventsPath()).isEqualTo("/custom/events");
    assertThat(properties.getOauthInstallPath()).isEqualTo("/custom/install");
    assertThat(properties.getOauthRedirectUriPath()).isEqualTo("/custom/redirect");
    assertThat(properties.getOauthCompletionUrl()).isEqualTo("https://example.com/done");
    assertThat(properties.getOauthCancellationUrl()).isEqualTo("https://example.com/cancel");
  }

  @Test
  void hasDefaults() {
    SlackProperties defaults = new SlackProperties();
    assertThat(defaults.getEventsPath()).isEqualTo(SlackProperties.DEFAULT_EVENTS_PATH);
    assertThat(defaults.getOauthInstallPath())
        .isEqualTo(SlackProperties.DEFAULT_OAUTH_INSTALL_PATH);
    assertThat(defaults.getOauthRedirectUriPath())
        .isEqualTo(SlackProperties.DEFAULT_OAUTH_REDIRECT_URI_PATH);
  }
}
