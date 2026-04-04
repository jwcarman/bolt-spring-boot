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

import org.jwcarman.slack.bolt.autoconfigure.method.MethodBindingFactory;
import org.jwcarman.slack.bolt.autoconfigure.parameter.ParameterBindingFactory;
import org.jwcarman.slack.bolt.autoconfigure.registrar.AnnotationDrivenAppCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jakarta_servlet.SlackAppServlet;
import com.slack.api.bolt.jakarta_servlet.SlackOAuthAppServlet;

/**
 * Autoconfiguration for the Slack Bolt framework. Creates and configures the Bolt {@link App},
 * registers event and OAuth servlets, and applies all {@link SlackAppCustomizer} beans.
 *
 * <p>Activated when {@code slack.signing-secret} is present. The mode (single-team vs OAuth) is
 * determined by which additional properties are set.
 *
 * @see SlackProperties
 * @see SlackAppCustomizer
 */
@AutoConfiguration
@EnableConfigurationProperties(SlackProperties.class)
@ConditionalOnProperty(prefix = "slack", name = "signing-secret")
public class SlackAutoConfiguration {

  /** Creates a new {@code SlackAutoConfiguration}. */
  public SlackAutoConfiguration() {}

  private static final Logger log = LoggerFactory.getLogger(SlackAutoConfiguration.class);

  /**
   * Creates the parameter binding factory with the available conversion service, falling back to
   * the shared default if none is configured.
   *
   * @param conversionServiceProvider optional conversion service for parameter type coercion
   * @return the parameter binding factory
   */
  @Bean
  public ParameterBindingFactory parameterBindingFactory(
      ObjectProvider<ConversionService> conversionServiceProvider) {
    return new ParameterBindingFactory(
        conversionServiceProvider.getIfAvailable(DefaultConversionService::getSharedInstance));
  }

  /**
   * Creates the method binding factory that delegates to the parameter binding factory.
   *
   * @param parameterBindingFactory the parameter binding factory
   * @return the method binding factory
   */
  @Bean
  public MethodBindingFactory methodBindingFactory(
      ParameterBindingFactory parameterBindingFactory) {
    return new MethodBindingFactory(parameterBindingFactory);
  }

  /**
   * Creates the annotation-driven customizer that registers handler methods.
   *
   * @param applicationContext the Spring application context
   * @param methodBindingFactory the factory used to create method bindings
   * @return the annotation-driven customizer
   */
  @Bean
  public AnnotationDrivenAppCustomizer annotationDrivenAppCustomizer(
      ApplicationContext applicationContext, MethodBindingFactory methodBindingFactory) {
    return new AnnotationDrivenAppCustomizer(applicationContext, methodBindingFactory);
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

  /** Single-team mode configuration, activated when {@code slack.bot-token} is set. */
  @Configuration
  @ConditionalOnProperty(prefix = "slack", name = "bot-token")
  static class SingleTeamConfiguration {

    /**
     * Creates the Bolt {@link AppConfig} for single-team mode.
     *
     * @param props the Slack configuration properties
     * @return the configured {@link AppConfig}
     */
    @Bean
    public AppConfig appConfig(SlackProperties props) {
      log.info("Configuring Slack Bolt in single-team mode");
      return AppConfig.builder()
          .signingSecret(props.getSigningSecret())
          .singleTeamBotToken(props.getBotToken())
          .build();
    }

    /**
     * Creates the Bolt {@link App} for single-team mode.
     *
     * @param config the Bolt application configuration
     * @param customizers the list of customizers to apply
     * @return the configured {@link App}
     */
    @Bean
    public App slackApp(AppConfig config, List<SlackAppCustomizer> customizers) {
      App app = new App(config);
      customizers.forEach(c -> c.customize(app));
      return app;
    }
  }

  /**
   * OAuth mode configuration, activated when {@code slack.client-id} and {@code
   * slack.client-secret} are set.
   */
  @Configuration
  @ConditionalOnProperty(
      prefix = "slack",
      name = {"client-id", "client-secret"})
  static class OAuthConfiguration {

    /**
     * Creates the Bolt {@link AppConfig} for OAuth mode.
     *
     * @param props the Slack configuration properties
     * @return the configured {@link AppConfig}
     */
    @Bean
    public AppConfig appConfig(SlackProperties props) {
      log.info("Configuring Slack Bolt in OAuth mode");
      return AppConfig.builder()
          .clientId(props.getClientId())
          .clientSecret(props.getClientSecret())
          .signingSecret(props.getSigningSecret())
          .scope(props.getScope())
          .userScope(props.getUserScope())
          .oauthInstallPath(props.getOauthInstallPath())
          .oauthRedirectUriPath(props.getOauthRedirectUriPath())
          .oauthCompletionUrl(props.getOauthCompletionUrl())
          .oauthCancellationUrl(props.getOauthCancellationUrl())
          .build();
    }

    /**
     * Creates the Bolt {@link App} for OAuth mode.
     *
     * @param config the Bolt application configuration
     * @param customizers the list of customizers to apply
     * @return the configured {@link App}
     */
    @Bean
    public App slackApp(AppConfig config, List<SlackAppCustomizer> customizers) {
      App app = new App(config).asOAuthApp(true);
      customizers.forEach(c -> c.customize(app));
      return app;
    }

    /**
     * Registers the Slack OAuth servlet at the configured install and redirect paths.
     *
     * @param app the Bolt application
     * @param props the Slack configuration properties
     * @return the servlet registration bean
     */
    @Bean
    public ServletRegistrationBean<SlackOAuthAppServlet> slackOAuthServlet(
        App app, SlackProperties props) {
      return new ServletRegistrationBean<>(
          new SlackOAuthAppServlet(app),
          props.getOauthInstallPath(),
          props.getOauthRedirectUriPath());
    }
  }
}
