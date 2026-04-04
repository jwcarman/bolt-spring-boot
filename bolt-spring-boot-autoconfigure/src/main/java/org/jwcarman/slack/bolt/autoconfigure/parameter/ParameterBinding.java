package org.jwcarman.slack.bolt.autoconfigure.parameter;

@FunctionalInterface
public interface ParameterBinding {
  Object resolve(Object request, Object context);
}
