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

@AutoConfiguration
@EnableConfigurationProperties(SlackProperties.class)
@ConditionalOnProperty(
    prefix = "slack",
    name = {"client-id", "client-secret", "signing-secret"})
public class SlackAutoConfiguration {

  @Bean
  public AppConfig appConfig(SlackProperties props) {
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

  @Bean
  public App slackApp(AppConfig config, List<SlackAppCustomizer> customizers) {
    App app = new App(config).asOAuthApp(true);
    customizers.forEach(c -> c.customize(app));
    return app;
  }

  @Bean
  public AnnotationDrivenAppCustomizer annotationDrivenAppCustomizer(
      ApplicationContext applicationContext) {
    return new AnnotationDrivenAppCustomizer(applicationContext);
  }

  @Bean
  public ServletRegistrationBean<SlackAppServlet> slackEventsServlet(
      App app, SlackProperties props) {
    return new ServletRegistrationBean<>(new SlackAppServlet(app), props.getEventsPath());
  }

  @Bean
  public ServletRegistrationBean<SlackOAuthAppServlet> slackOAuthServlet(
      App app, SlackProperties props) {
    return new ServletRegistrationBean<>(
        new SlackOAuthAppServlet(app),
        props.getOauthInstallPath(),
        props.getOauthRedirectUriPath());
  }
}
