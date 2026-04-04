package org.jwcarman.slack.bolt.autoconfigure.method;

import com.slack.api.bolt.context.Context;
import com.slack.api.bolt.response.Response;

public interface MethodBinding<R, C extends Context> {

  Response invoke(R request, C ctx);
}
