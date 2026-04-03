package org.jwcarman.slack.bolt.autoconfigure;

import com.slack.api.bolt.App;

@FunctionalInterface
public interface SlackAppCustomizer {
  void customize(App app);
}
