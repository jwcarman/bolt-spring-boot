package org.jwcarman.slack.bolt.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
public class SlackProperties {

  private String clientId;
  private String clientSecret;
  private String signingSecret;
  private String scope;
  private String userScope;
  private String eventsPath = "/slack/events";
  private String oauthInstallPath = "/slack/install";
  private String oauthRedirectUriPath = "/slack/oauth_redirect";
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
