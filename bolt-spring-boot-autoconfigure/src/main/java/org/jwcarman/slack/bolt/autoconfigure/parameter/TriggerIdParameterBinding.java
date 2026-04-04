package org.jwcarman.slack.bolt.autoconfigure.parameter;

import com.slack.api.app_backend.interactive_components.payload.AttachmentActionPayload;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.payload.GlobalShortcutPayload;
import com.slack.api.app_backend.interactive_components.payload.MessageShortcutPayload;
import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.app_backend.views.payload.ViewSubmissionPayload;

public final class TriggerIdParameterBinding implements ParameterBinding {

  // ------------------------ INTERFACE METHODS ------------------------

  // --------------------- Interface ParameterBinding ---------------------

  @Override
  public String resolve(Object payload, Object context) {
    return switch (payload) {
      case SlashCommandPayload p -> p.getTriggerId();
      case BlockActionPayload p -> p.getTriggerId();
      case ViewSubmissionPayload p -> p.getTriggerId();
      case GlobalShortcutPayload p -> p.getTriggerId();
      case MessageShortcutPayload p -> p.getTriggerId();
      case AttachmentActionPayload p -> p.getTriggerId();
      default ->
          throw new IllegalArgumentException(
              "@TriggerId not supported for payload type " + payload.getClass().getSimpleName());
    };
  }
}
