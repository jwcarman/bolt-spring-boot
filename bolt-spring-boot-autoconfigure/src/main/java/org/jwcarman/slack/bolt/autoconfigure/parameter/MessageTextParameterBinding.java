package org.jwcarman.slack.bolt.autoconfigure.parameter;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;

public final class MessageTextParameterBinding implements ParameterBinding {

  // ------------------------ INTERFACE METHODS ------------------------

  // --------------------- Interface ParameterBinding ---------------------

  @Override
  public String resolve(Object payload, Object context) {
    return switch (payload) {
      case EventsApiPayload<?> p -> {
        var event = p.getEvent();
        yield switch (event) {
          case MessageEvent m -> m.getText();
          case AppMentionEvent m -> m.getText();
          default ->
              throw new IllegalArgumentException(
                  "@MessageText not supported for event type " + event.getClass().getSimpleName());
        };
      }
      default ->
          throw new IllegalArgumentException(
              "@MessageText not supported for payload type " + payload.getClass().getSimpleName());
    };
  }
}
