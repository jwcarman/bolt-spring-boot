package org.jwcarman.slack.bolt.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class SlackHandlerInvocationExceptionTest {

  @Test
  void shouldPreserveMessageAndCause() {
    Throwable cause = new IllegalAccessException("not accessible");
    SlackHandlerInvocationException exception =
        new SlackHandlerInvocationException("Failed to invoke handler: myMethod", cause);

    assertThat(exception.getMessage()).isEqualTo("Failed to invoke handler: myMethod");
    assertThat(exception.getCause()).isSameAs(cause);
  }
}
