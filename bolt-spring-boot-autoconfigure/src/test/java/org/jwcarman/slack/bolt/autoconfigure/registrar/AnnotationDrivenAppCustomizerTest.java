/*
 * Copyright © 2026 James Carman
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jwcarman.slack.bolt.autoconfigure.registrar;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.jwcarman.slack.bolt.autoconfigure.SlackHandlerInvocationException;
import org.jwcarman.slack.bolt.autoconfigure.annotations.AttachmentAction;
import org.jwcarman.slack.bolt.autoconfigure.annotations.BlockAction;
import org.jwcarman.slack.bolt.autoconfigure.annotations.BlockSuggestion;
import org.jwcarman.slack.bolt.autoconfigure.annotations.DialogCancellation;
import org.jwcarman.slack.bolt.autoconfigure.annotations.DialogSubmission;
import org.jwcarman.slack.bolt.autoconfigure.annotations.DialogSuggestion;
import org.jwcarman.slack.bolt.autoconfigure.annotations.Event;
import org.jwcarman.slack.bolt.autoconfigure.annotations.GlobalShortcut;
import org.jwcarman.slack.bolt.autoconfigure.annotations.Message;
import org.jwcarman.slack.bolt.autoconfigure.annotations.MessageShortcut;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlackController;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlashCommand;
import org.jwcarman.slack.bolt.autoconfigure.annotations.ViewClosed;
import org.jwcarman.slack.bolt.autoconfigure.annotations.ViewSubmission;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.context.builtin.ActionContext;
import com.slack.api.bolt.context.builtin.AttachmentActionContext;
import com.slack.api.bolt.context.builtin.BlockSuggestionContext;
import com.slack.api.bolt.context.builtin.DefaultContext;
import com.slack.api.bolt.context.builtin.DialogCancellationContext;
import com.slack.api.bolt.context.builtin.DialogSubmissionContext;
import com.slack.api.bolt.context.builtin.DialogSuggestionContext;
import com.slack.api.bolt.context.builtin.EventContext;
import com.slack.api.bolt.context.builtin.GlobalShortcutContext;
import com.slack.api.bolt.context.builtin.MessageShortcutContext;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.context.builtin.ViewSubmissionContext;
import com.slack.api.bolt.handler.builtin.SlashCommandHandler;
import com.slack.api.bolt.request.builtin.AttachmentActionRequest;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.BlockSuggestionRequest;
import com.slack.api.bolt.request.builtin.DialogCancellationRequest;
import com.slack.api.bolt.request.builtin.DialogSubmissionRequest;
import com.slack.api.bolt.request.builtin.DialogSuggestionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewClosedRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.bolt.response.Response;
import com.slack.api.model.event.AppMentionEvent;
import com.slack.api.model.event.MessageEvent;

class AnnotationDrivenAppCustomizerTest {

  private App createTestApp() {
    AppConfig config =
        AppConfig.builder().singleTeamBotToken("xoxb-test").signingSecret("test-secret").build();
    return new App(config);
  }

  private Map<?, ?> getHandlerMap(App app, String fieldName) throws Exception {
    Field field = App.class.getDeclaredField(fieldName);
    field.setAccessible(true);
    return (Map<?, ?>) field.get(app);
  }

  // --- Handler beans ---

  @SlackController
  public static class TestSlashCommandHandler {
    @SlashCommand("/test")
    public Response handle(SlashCommandRequest req, SlashCommandContext ctx) {
      return ctx.ack("test response");
    }
  }

  @SlackController
  public static class TestEventHandler {
    @Event(AppMentionEvent.class)
    public Response handle(EventsApiPayload<AppMentionEvent> payload, EventContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestBlockActionHandler {
    @BlockAction("action-id")
    public Response handle(BlockActionRequest req, ActionContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestBlockSuggestionHandler {
    @BlockSuggestion("suggestion-id")
    public Response handle(BlockSuggestionRequest req, BlockSuggestionContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestGlobalShortcutHandler {
    @GlobalShortcut("shortcut-id")
    public Response handle(GlobalShortcutRequest req, GlobalShortcutContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestMessageShortcutHandler {
    @MessageShortcut("msg-shortcut-id")
    public Response handle(MessageShortcutRequest req, MessageShortcutContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestViewSubmissionHandler {
    @ViewSubmission("view-callback")
    public Response handle(ViewSubmissionRequest req, ViewSubmissionContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestViewClosedHandler {
    @ViewClosed("view-closed-callback")
    public Response handle(ViewClosedRequest req, DefaultContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestMessageHandler {
    @Message("hello.*world")
    public Response handle(EventsApiPayload<MessageEvent> payload, EventContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestDialogSubmissionHandler {
    @DialogSubmission("dialog-callback")
    public Response handle(DialogSubmissionRequest req, DialogSubmissionContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestDialogSuggestionHandler {
    @DialogSuggestion("dialog-suggestion")
    public Response handle(DialogSuggestionRequest req, DialogSuggestionContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestDialogCancellationHandler {
    @DialogCancellation("dialog-cancel")
    public Response handle(DialogCancellationRequest req, DialogCancellationContext ctx) {
      return ctx.ack();
    }
  }

  @SlackController
  public static class TestAttachmentActionHandler {
    @AttachmentAction("attachment-callback")
    public Response handle(AttachmentActionRequest req, AttachmentActionContext ctx) {
      return ctx.ack();
    }
  }

  // --- Tests ---

  @Test
  void shouldRegisterSlashCommandFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestSlashCommandHandler.class);
    assertThat(getHandlerMap(app, "slashCommandHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterEventFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestEventHandler.class);
    assertThat(getHandlerMap(app, "eventHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterBlockActionFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestBlockActionHandler.class);
    assertThat(getHandlerMap(app, "blockActionHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterBlockSuggestionFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestBlockSuggestionHandler.class);
    assertThat(getHandlerMap(app, "blockSuggestionHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterGlobalShortcutFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestGlobalShortcutHandler.class);
    assertThat(getHandlerMap(app, "globalShortcutHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterMessageShortcutFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestMessageShortcutHandler.class);
    assertThat(getHandlerMap(app, "messageShortcutHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterViewSubmissionFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestViewSubmissionHandler.class);
    assertThat(getHandlerMap(app, "viewSubmissionHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterViewClosedFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestViewClosedHandler.class);
    assertThat(getHandlerMap(app, "viewClosedHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterMessageFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestMessageHandler.class);
    assertThat(getHandlerMap(app, "eventHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterDialogSubmissionFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestDialogSubmissionHandler.class);
    assertThat(getHandlerMap(app, "dialogSubmissionHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterDialogSuggestionFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestDialogSuggestionHandler.class);
    assertThat(getHandlerMap(app, "dialogSuggestionHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterDialogCancellationFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestDialogCancellationHandler.class);
    assertThat(getHandlerMap(app, "dialogCancellationHandlers")).isNotEmpty();
  }

  @Test
  void shouldRegisterAttachmentActionFromAnnotatedMethod() throws Exception {
    App app = customizeWithBean(TestAttachmentActionHandler.class);
    assertThat(getHandlerMap(app, "attachmentActionHandlers")).isNotEmpty();
  }

  @SlackController
  public static class ThrowingSlashCommandHandler {
    @SlashCommand("/throws")
    public Response handle(SlashCommandRequest req, SlashCommandContext ctx) throws Exception {
      throw new Exception("something went wrong");
    }
  }

  @Test
  void shouldWrapCheckedExceptionInSlackHandlerInvocationException() throws Exception {
    App app = customizeWithBean(ThrowingSlashCommandHandler.class);
    Map<?, ?> handlers = getHandlerMap(app, "slashCommandHandlers");
    SlashCommandHandler handler = (SlashCommandHandler) handlers.values().iterator().next();

    assertThatThrownBy(() -> handler.apply(null, null))
        .isInstanceOf(SlackHandlerInvocationException.class)
        .hasMessageContaining("handle")
        .hasCauseInstanceOf(InvocationTargetException.class);
  }

  private App customizeWithBean(Class<?> handlerClass) {
    AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
    context.registerBean(handlerClass);
    context.refresh();

    AnnotationDrivenAppCustomizer customizer = new AnnotationDrivenAppCustomizer(context);
    App app = createTestApp();
    customizer.customize(app);

    context.close();
    return app;
  }
}
