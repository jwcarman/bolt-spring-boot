package org.jwcarman.slack.bolt.autoconfigure.method;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

/**
 * Invokes a handler method with resolved parameters and converts the return value to a Response.
 *
 * @param <R> the request type
 * @param <C> the context type
 */
public interface MethodBinding<R, C extends Context> {

  /**
   * Invokes the bound handler method.
   *
   * @param request the Slack request
   * @param ctx the Bolt context
   * @return the handler response
   */
  Response invoke(R request, C ctx);
}
