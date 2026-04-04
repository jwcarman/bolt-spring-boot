package org.jwcarman.slack.bolt.autoconfigure.parameter;

import com.slack.api.app_backend.dialogs.payload.DialogCancellationPayload;
import com.slack.api.app_backend.dialogs.payload.DialogSubmissionPayload;
import com.slack.api.app_backend.dialogs.payload.DialogSuggestionPayload;
import com.slack.api.app_backend.interactive_components.payload.AttachmentActionPayload;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.payload.BlockSuggestionPayload;
import com.slack.api.app_backend.interactive_components.payload.GlobalShortcutPayload;
import com.slack.api.app_backend.interactive_components.payload.MessageShortcutPayload;
import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.app_backend.views.payload.ViewClosedPayload;
import com.slack.api.app_backend.views.payload.ViewSubmissionPayload;

public final class UserNameParameterBinding implements ParameterBinding {

  // ------------------------ INTERFACE METHODS ------------------------

  // --------------------- Interface ParameterBinding ---------------------

  @Override
  public String resolve(Object payload, Object context) {
    return switch (payload) {
      case SlashCommandPayload p -> p.getUserName();
      case BlockActionPayload p -> p.getUser().getUsername();
      case DialogSubmissionPayload p -> p.getUser().getName();
      case DialogSuggestionPayload p -> p.getUser().getName();
      case DialogCancellationPayload p -> p.getUser().getName();
      case AttachmentActionPayload p -> p.getUser().getName();
      case BlockSuggestionPayload p -> p.getUser().getName();
      case ViewSubmissionPayload p -> p.getUser().getName();
      case ViewClosedPayload p -> p.getUser().getName();
      case GlobalShortcutPayload p -> p.getUser().getUsername();
      case MessageShortcutPayload p -> p.getUser().getName();
      default ->
          throw new IllegalArgumentException(
              "@UserName not supported for payload type " + payload.getClass().getSimpleName());
    };
  }
}
