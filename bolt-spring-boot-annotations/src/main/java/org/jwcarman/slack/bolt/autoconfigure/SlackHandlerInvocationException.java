package org.jwcarman.slack.bolt.autoconfigure;

public class SlackHandlerInvocationException extends RuntimeException {

  public SlackHandlerInvocationException(String message, Throwable cause) {
    super(message, cause);
  }
}
