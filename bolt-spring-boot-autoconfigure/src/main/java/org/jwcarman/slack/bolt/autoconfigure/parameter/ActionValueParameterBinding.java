package org.jwcarman.slack.bolt.autoconfigure.parameter;

import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;

public final class ActionValueParameterBinding implements ParameterBinding {

  // ------------------------ INTERFACE METHODS ------------------------

  // --------------------- Interface ParameterBinding ---------------------

  @Override
  public String resolve(Object payload, Object context) {
    return switch (payload) {
      case BlockActionPayload p -> p.getActions().get(0).getValue();
      default ->
          throw new IllegalArgumentException(
              "@ActionValue not supported for payload type " + payload.getClass().getSimpleName());
    };
  }
}
