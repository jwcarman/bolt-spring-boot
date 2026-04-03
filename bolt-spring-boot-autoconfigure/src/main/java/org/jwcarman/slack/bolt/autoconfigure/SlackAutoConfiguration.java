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

import java.util.List;

import org.jwcarman.slack.bolt.autoconfigure.registrar.AnnotationDrivenAppCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jakarta_servlet.SlackAppServlet;
import com.slack.api.bolt.jakarta_servlet.SlackOAuthAppServlet;

/**
 * Auto-configuration for the Slack Bolt framework. Creates and configures the Bolt {@link App},
 * registers event and OAuth servlets, and applies all {@link SlackAppCustomizer} beans.
 *
 * <p>Activated when {@code slack.client-id}, {@code slack.client-secret}, and {@code
 * slack.signing-secret} are all present in the environment.
 *
 * @see SlackProperties
 * @see SlackAppCustomizer
 */
@AutoConfiguration
@EnableConfigurationProperties(SlackProperties.class)
@ConditionalOnProperty(prefix = "slack", name = "signing-secret")
public class SlackAutoConfiguration {

  private static final Logger log = LoggerFactory.getLogger(SlackAutoConfiguration.class);

  /**
   * Creates the Bolt {@link AppConfig} from the bound {@link SlackProperties}.
   *
   * <p>When {@code slack.bot-token} is set, the app runs in single-team mode. When {@code
   * slack.client-id} and {@code slack.client-secret} are set, the app runs in OAuth mode.
   *
   * @param props the Slack configuration properties
   * @return the configured {@link AppConfig}
   */
  @Bean
  public AppConfig appConfig(SlackProperties props) {
    AppConfig.AppConfigBuilder builder =
        AppConfig.builder().signingSecret(props.getSigningSecret());

    if (props.getBotToken() != null) {
      log.info("Configuring Slack Bolt in single-team mode");
      builder.singleTeamBotToken(props.getBotToken());
    } else {
      log.info("Configuring Slack Bolt in OAuth mode");
      builder
          .clientId(props.getClientId())
          .clientSecret(props.getClientSecret())
          .scope(props.getScope())
          .userScope(props.getUserScope())
          .oauthInstallPath(props.getOauthInstallPath())
          .oauthRedirectUriPath(props.getOauthRedirectUriPath())
          .oauthCompletionUrl(props.getOauthCompletionUrl())
          .oauthCancellationUrl(props.getOauthCancellationUrl());
    }

    return builder.build();
  }

  /**
   * Creates and initializes the Bolt {@link App}, applying all registered customizers.
   *
   * <p>In OAuth mode, the app is configured as an OAuth app. In single-team mode, the app runs
   * without OAuth.
   *
   * @param config the Bolt application configuration
   * @param props the Slack configuration properties
   * @param customizers the list of customizers to apply
   * @return the configured {@link App}
   */
  @Bean
  public App slackApp(
      AppConfig config, SlackProperties props, List<SlackAppCustomizer> customizers) {
    App app = new App(config);
    if (props.getBotToken() == null) {
      app.asOAuthApp(true);
    }
    customizers.forEach(c -> c.customize(app));
    return app;
  }

  /**
   * Creates the annotation-driven customizer that registers handler methods.
   *
   * @param applicationContext the Spring application context
   * @return the annotation-driven customizer
   */
  @Bean
  public AnnotationDrivenAppCustomizer annotationDrivenAppCustomizer(
      ApplicationContext applicationContext) {
    return new AnnotationDrivenAppCustomizer(applicationContext);
  }

  /**
   * Registers the Slack events servlet at the configured events path.
   *
   * @param app the Bolt application
   * @param props the Slack configuration properties
   * @return the servlet registration bean
   */
  @Bean
  public ServletRegistrationBean<SlackAppServlet> slackEventsServlet(
      App app, SlackProperties props) {
    return new ServletRegistrationBean<>(new SlackAppServlet(app), props.getEventsPath());
  }

  /**
   * Registers the Slack OAuth servlet at the configured install and redirect paths. Only activated
   * when OAuth properties are present (i.e., not in single-team bot token mode).
   *
   * @param app the Bolt application
   * @param props the Slack configuration properties
   * @return the servlet registration bean
   */
  @Bean
  @ConditionalOnProperty(
      prefix = "slack",
      name = {"client-id", "client-secret"})
  public ServletRegistrationBean<SlackOAuthAppServlet> slackOAuthServlet(
      App app, SlackProperties props) {
    return new ServletRegistrationBean<>(
        new SlackOAuthAppServlet(app),
        props.getOauthInstallPath(),
        props.getOauthRedirectUriPath());
  }
}
