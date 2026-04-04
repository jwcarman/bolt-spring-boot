package org.jwcarman.slack.bolt.autoconfigure.parameter;

import com.slack.api.app_backend.dialogs.payload.DialogCancellationPayload;
import com.slack.api.app_backend.dialogs.payload.DialogSubmissionPayload;
import com.slack.api.app_backend.interactive_components.payload.AttachmentActionPayload;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.payload.MessageShortcutPayload;
import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;

public final class ResponseUrlParameterBinding implements ParameterBinding {

  // ------------------------ INTERFACE METHODS ------------------------

  // --------------------- Interface ParameterBinding ---------------------

  @Override
  public String resolve(Object payload, Object context) {
    return switch (payload) {
      case SlashCommandPayload p -> p.getResponseUrl();
      case BlockActionPayload p -> p.getResponseUrl();
      case MessageShortcutPayload p -> p.getResponseUrl();
      case DialogSubmissionPayload p -> p.getResponseUrl();
      case DialogCancellationPayload p -> p.getResponseUrl();
      case AttachmentActionPayload p -> p.getResponseUrl();
      default ->
          throw new IllegalArgumentException(
              "@ResponseUrl not supported for payload type " + payload.getClass().getSimpleName());
    };
  }
}
