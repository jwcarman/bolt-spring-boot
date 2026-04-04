package org.jwcarman.slack.bolt.autoconfigure.parameter;

import com.slack.api.app_backend.dialogs.payload.DialogCancellationPayload;
import com.slack.api.app_backend.dialogs.payload.DialogSubmissionPayload;
import com.slack.api.app_backend.dialogs.payload.DialogSuggestionPayload;
import com.slack.api.app_backend.interactive_components.payload.AttachmentActionPayload;
import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.interactive_components.payload.BlockSuggestionPayload;
import com.slack.api.app_backend.interactive_components.payload.MessageShortcutPayload;
import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;

public final class ChannelIdParameterBinding implements ParameterBinding {

  // ------------------------ INTERFACE METHODS ------------------------

  // --------------------- Interface ParameterBinding ---------------------

  @Override
  public String resolve(Object payload, Object context) {
    return switch (payload) {
      case SlashCommandPayload p -> p.getChannelId();
      case BlockActionPayload p -> p.getChannel().getId();
      case MessageShortcutPayload p -> p.getChannel().getId();
      case DialogSubmissionPayload p -> p.getChannel().getId();
      case DialogSuggestionPayload p -> p.getChannel().getId();
      case DialogCancellationPayload p -> p.getChannel().getId();
      case AttachmentActionPayload p -> p.getChannel().getId();
      case BlockSuggestionPayload p -> p.getChannel().getId();
      default ->
          throw new IllegalArgumentException(
              "@ChannelId not supported for payload type " + payload.getClass().getSimpleName());
    };
  }
}
