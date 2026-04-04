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

public final class TeamIdParameterBinding implements ParameterBinding {

  // ------------------------ INTERFACE METHODS ------------------------

  // --------------------- Interface ParameterBinding ---------------------

  @Override
  public String resolve(Object payload, Object context) {
    return switch (payload) {
      case SlashCommandPayload p -> p.getTeamId();
      case BlockActionPayload p -> p.getTeam().getId();
      case ViewSubmissionPayload p -> p.getTeam().getId();
      case GlobalShortcutPayload p -> p.getTeam().getId();
      case MessageShortcutPayload p -> p.getTeam().getId();
      case DialogSubmissionPayload p -> p.getTeam().getId();
      case DialogSuggestionPayload p -> p.getTeam().getId();
      case DialogCancellationPayload p -> p.getTeam().getId();
      case AttachmentActionPayload p -> p.getTeam().getId();
      case BlockSuggestionPayload p -> p.getTeam().getId();
      case ViewClosedPayload p -> p.getTeam().getId();
      default ->
          throw new IllegalArgumentException(
              "@TeamId not supported for payload type " + payload.getClass().getSimpleName());
    };
  }
}
