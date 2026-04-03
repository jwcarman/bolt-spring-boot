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

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the Slack Bolt Spring Boot starter, bound to the {@code slack.*}
 * prefix.
 *
 * @see SlackAutoConfiguration
 */
@ConfigurationProperties(prefix = "slack")
public class SlackProperties {

  public static final String DEFAULT_EVENTS_PATH = "/slack/events";
  public static final String DEFAULT_OAUTH_INSTALL_PATH = "/slack/install";
  public static final String DEFAULT_OAUTH_REDIRECT_URI_PATH = "/slack/oauth_redirect";

  private String clientId;
  private String clientSecret;
  private String signingSecret;
  private String scope;
  private String userScope;
  private String eventsPath = DEFAULT_EVENTS_PATH;
  private String oauthInstallPath = DEFAULT_OAUTH_INSTALL_PATH;
  private String oauthRedirectUriPath = DEFAULT_OAUTH_REDIRECT_URI_PATH;
  private String oauthCompletionUrl;
  private String oauthCancellationUrl;

  public String getClientId() {
    return clientId;
  }

  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  public String getClientSecret() {
    return clientSecret;
  }

  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  public String getSigningSecret() {
    return signingSecret;
  }

  public void setSigningSecret(String signingSecret) {
    this.signingSecret = signingSecret;
  }

  public String getScope() {
    return scope;
  }

  public void setScope(String scope) {
    this.scope = scope;
  }

  public String getUserScope() {
    return userScope;
  }

  public void setUserScope(String userScope) {
    this.userScope = userScope;
  }

  public String getEventsPath() {
    return eventsPath;
  }

  public void setEventsPath(String eventsPath) {
    this.eventsPath = eventsPath;
  }

  public String getOauthInstallPath() {
    return oauthInstallPath;
  }

  public void setOauthInstallPath(String oauthInstallPath) {
    this.oauthInstallPath = oauthInstallPath;
  }

  public String getOauthRedirectUriPath() {
    return oauthRedirectUriPath;
  }

  public void setOauthRedirectUriPath(String oauthRedirectUriPath) {
    this.oauthRedirectUriPath = oauthRedirectUriPath;
  }

  public String getOauthCompletionUrl() {
    return oauthCompletionUrl;
  }

  public void setOauthCompletionUrl(String oauthCompletionUrl) {
    this.oauthCompletionUrl = oauthCompletionUrl;
  }

  public String getOauthCancellationUrl() {
    return oauthCancellationUrl;
  }

  public void setOauthCancellationUrl(String oauthCancellationUrl) {
    this.oauthCancellationUrl = oauthCancellationUrl;
  }
}
