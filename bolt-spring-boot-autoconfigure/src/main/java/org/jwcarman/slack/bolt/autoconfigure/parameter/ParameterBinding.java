package org.jwcarman.slack.bolt.autoconfigure.parameter;

/** Resolves a handler method parameter value from the request and context. */
@FunctionalInterface
public interface ParameterBinding {

  /**
   * Resolves the parameter value from the given request and context.
   *
   * @param request the Slack request object
   * @param context the Bolt context object
   * @return the resolved parameter value
   */
  Object resolve(Object request, Object context);
}
