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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

import org.jwcarman.slack.bolt.autoconfigure.SlackAppCustomizer;
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
import org.jwcarman.slack.bolt.autoconfigure.method.MethodBindingFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.slack.api.app_backend.events.payload.EventsApiPayload;
import com.slack.api.bolt.App;
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

public class AnnotationDrivenAppCustomizer implements SlackAppCustomizer {

  private final ApplicationContext applicationContext;
  private final MethodBindingFactory methodBindingFactory;

  /**
   * Creates a new customizer that scans for annotated handler methods.
   *
   * @param applicationContext the Spring application context to scan for beans
   * @param methodBindingFactory the factory used to create method bindings
   */
  public AnnotationDrivenAppCustomizer(
      ApplicationContext applicationContext, MethodBindingFactory methodBindingFactory) {
    this.applicationContext = applicationContext;
    this.methodBindingFactory = methodBindingFactory;
  }

  @Override
  public void customize(App app) {
    applicationContext
        .getBeansWithAnnotation(SlackController.class)
        .values()
        .forEach(
            bean -> {
              registerSlashCommands(app, bean);
              registerEvents(app, bean);
              registerBlockActions(app, bean);
              registerBlockSuggestions(app, bean);
              registerGlobalShortcuts(app, bean);
              registerMessageShortcuts(app, bean);
              registerViewSubmissions(app, bean);
              registerViewsClosed(app, bean);
              registerMessages(app, bean);
              registerDialogSubmissions(app, bean);
              registerDialogSuggestions(app, bean);
              registerDialogCancellations(app, bean);
              registerAttachmentActions(app, bean);
            });
  }

  private void registerSlashCommands(App app, Object bean) {
    scanAndRegister(
        bean,
        SlashCommand.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, SlashCommandRequest.class, SlashCommandContext.class);
          app.command(annotation.value(), binding::invoke);
        });
  }

  private void registerEvents(App app, Object bean) {
    scanAndRegister(
        bean,
        Event.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(bean, method, EventsApiPayload.class, EventContext.class);
          app.event(annotation.value(), binding::invoke);
        });
  }

  private void registerBlockActions(App app, Object bean) {
    scanAndRegister(
        bean,
        BlockAction.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, BlockActionRequest.class, ActionContext.class);
          app.blockAction(annotation.value(), binding::invoke);
        });
  }

  private void registerBlockSuggestions(App app, Object bean) {
    scanAndRegister(
        bean,
        BlockSuggestion.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, BlockSuggestionRequest.class, BlockSuggestionContext.class);
          app.blockSuggestion(annotation.value(), binding::invoke);
        });
  }

  private void registerGlobalShortcuts(App app, Object bean) {
    scanAndRegister(
        bean,
        GlobalShortcut.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, GlobalShortcutRequest.class, GlobalShortcutContext.class);
          app.globalShortcut(annotation.value(), binding::invoke);
        });
  }

  private void registerMessageShortcuts(App app, Object bean) {
    scanAndRegister(
        bean,
        MessageShortcut.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, MessageShortcutRequest.class, MessageShortcutContext.class);
          app.messageShortcut(annotation.value(), binding::invoke);
        });
  }

  private void registerViewSubmissions(App app, Object bean) {
    scanAndRegister(
        bean,
        ViewSubmission.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, ViewSubmissionRequest.class, ViewSubmissionContext.class);
          app.viewSubmission(annotation.value(), binding::invoke);
        });
  }

  private void registerViewsClosed(App app, Object bean) {
    scanAndRegister(
        bean,
        ViewClosed.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, ViewClosedRequest.class, DefaultContext.class);
          app.viewClosed(annotation.value(), binding::invoke);
        });
  }

  private void registerMessages(App app, Object bean) {
    scanAndRegister(
        bean,
        Message.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(bean, method, EventsApiPayload.class, EventContext.class);
          app.message(Pattern.compile(annotation.value()), binding::invoke);
        });
  }

  private void registerDialogSubmissions(App app, Object bean) {
    scanAndRegister(
        bean,
        DialogSubmission.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, DialogSubmissionRequest.class, DialogSubmissionContext.class);
          app.dialogSubmission(annotation.value(), binding::invoke);
        });
  }

  private void registerDialogSuggestions(App app, Object bean) {
    scanAndRegister(
        bean,
        DialogSuggestion.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, DialogSuggestionRequest.class, DialogSuggestionContext.class);
          app.dialogSuggestion(annotation.value(), binding::invoke);
        });
  }

  private void registerDialogCancellations(App app, Object bean) {
    scanAndRegister(
        bean,
        DialogCancellation.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, DialogCancellationRequest.class, DialogCancellationContext.class);
          app.dialogCancellation(annotation.value(), binding::invoke);
        });
  }

  private void registerAttachmentActions(App app, Object bean) {
    scanAndRegister(
        bean,
        AttachmentAction.class,
        (annotation, method) -> {
          var binding =
              methodBindingFactory.create(
                  bean, method, AttachmentActionRequest.class, AttachmentActionContext.class);
          app.attachmentAction(annotation.value(), binding::invoke);
        });
  }

  private <A extends Annotation> void scanAndRegister(
      Object bean, Class<A> annotationType, HandlerRegistrar<A> registrar) {
    Map<Method, A> methods =
        MethodIntrospector.selectMethods(
            bean.getClass(),
            (MethodIntrospector.MetadataLookup<A>)
                method -> AnnotatedElementUtils.findMergedAnnotation(method, annotationType));
    methods.forEach((method, annotation) -> registrar.register(annotation, method));
  }

  @FunctionalInterface
  private interface HandlerRegistrar<A extends Annotation> {
    void register(A annotation, Method method);
  }
}
