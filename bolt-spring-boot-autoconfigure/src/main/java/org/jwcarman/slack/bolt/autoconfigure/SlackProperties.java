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

  /** Default servlet path for Slack events. */
  public static final String DEFAULT_EVENTS_PATH = "/slack/events";

  /** Default servlet path for the OAuth install endpoint. */
  public static final String DEFAULT_OAUTH_INSTALL_PATH = "/slack/install";

  /** Default servlet path for the OAuth redirect URI. */
  public static final String DEFAULT_OAUTH_REDIRECT_URI_PATH = "/slack/oauth_redirect";

  private String botToken;
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

  /**
   * Returns the single-team bot token for non-OAuth apps.
   *
   * @return the bot token, or {@code null} if using OAuth mode
   */
  public String getBotToken() {
    return botToken;
  }

  /**
   * Sets the single-team bot token for non-OAuth apps.
   *
   * @param botToken the bot token
   */
  public void setBotToken(String botToken) {
    this.botToken = botToken;
  }

  /**
   * Returns the Slack app client ID.
   *
   * @return the client ID
   */
  public String getClientId() {
    return clientId;
  }

  /**
   * Sets the Slack app client ID.
   *
   * @param clientId the client ID
   */
  public void setClientId(String clientId) {
    this.clientId = clientId;
  }

  /**
   * Returns the Slack app client secret.
   *
   * @return the client secret
   */
  public String getClientSecret() {
    return clientSecret;
  }

  /**
   * Sets the Slack app client secret.
   *
   * @param clientSecret the client secret
   */
  public void setClientSecret(String clientSecret) {
    this.clientSecret = clientSecret;
  }

  /**
   * Returns the Slack app signing secret.
   *
   * @return the signing secret
   */
  public String getSigningSecret() {
    return signingSecret;
  }

  /**
   * Sets the Slack app signing secret.
   *
   * @param signingSecret the signing secret
   */
  public void setSigningSecret(String signingSecret) {
    this.signingSecret = signingSecret;
  }

  /**
   * Returns the OAuth scope.
   *
   * @return the scope
   */
  public String getScope() {
    return scope;
  }

  /**
   * Sets the OAuth scope.
   *
   * @param scope the scope
   */
  public void setScope(String scope) {
    this.scope = scope;
  }

  /**
   * Returns the OAuth user scope.
   *
   * @return the user scope
   */
  public String getUserScope() {
    return userScope;
  }

  /**
   * Sets the OAuth user scope.
   *
   * @param userScope the user scope
   */
  public void setUserScope(String userScope) {
    this.userScope = userScope;
  }

  /**
   * Returns the servlet path for Slack events.
   *
   * @return the events path
   */
  public String getEventsPath() {
    return eventsPath;
  }

  /**
   * Sets the servlet path for Slack events.
   *
   * @param eventsPath the events path
   */
  public void setEventsPath(String eventsPath) {
    this.eventsPath = eventsPath;
  }

  /**
   * Returns the servlet path for the OAuth install endpoint.
   *
   * @return the OAuth install path
   */
  public String getOauthInstallPath() {
    return oauthInstallPath;
  }

  /**
   * Sets the servlet path for the OAuth install endpoint.
   *
   * @param oauthInstallPath the OAuth install path
   */
  public void setOauthInstallPath(String oauthInstallPath) {
    this.oauthInstallPath = oauthInstallPath;
  }

  /**
   * Returns the servlet path for the OAuth redirect URI.
   *
   * @return the OAuth redirect URI path
   */
  public String getOauthRedirectUriPath() {
    return oauthRedirectUriPath;
  }

  /**
   * Sets the servlet path for the OAuth redirect URI.
   *
   * @param oauthRedirectUriPath the OAuth redirect URI path
   */
  public void setOauthRedirectUriPath(String oauthRedirectUriPath) {
    this.oauthRedirectUriPath = oauthRedirectUriPath;
  }

  /**
   * Returns the URL to redirect to after successful OAuth completion.
   *
   * @return the OAuth completion URL
   */
  public String getOauthCompletionUrl() {
    return oauthCompletionUrl;
  }

  /**
   * Sets the URL to redirect to after successful OAuth completion.
   *
   * @param oauthCompletionUrl the OAuth completion URL
   */
  public void setOauthCompletionUrl(String oauthCompletionUrl) {
    this.oauthCompletionUrl = oauthCompletionUrl;
  }

  /**
   * Returns the URL to redirect to after OAuth cancellation.
   *
   * @return the OAuth cancellation URL
   */
  public String getOauthCancellationUrl() {
    return oauthCancellationUrl;
  }

  /**
   * Sets the URL to redirect to after OAuth cancellation.
   *
   * @param oauthCancellationUrl the OAuth cancellation URL
   */
  public void setOauthCancellationUrl(String oauthCancellationUrl) {
    this.oauthCancellationUrl = oauthCancellationUrl;
  }
}
