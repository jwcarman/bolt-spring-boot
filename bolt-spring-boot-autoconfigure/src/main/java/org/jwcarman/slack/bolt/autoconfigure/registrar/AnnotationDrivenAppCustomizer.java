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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.regex.Pattern;

import org.jwcarman.slack.bolt.autoconfigure.SlackAppCustomizer;
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
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;

import com.slack.api.bolt.App;
import com.slack.api.bolt.response.Response;

/**
 * A {@link SlackAppCustomizer} that scans {@link SlackController @SlackController} beans for
 * annotated handler methods and registers them with the Bolt {@link App}.
 *
 * <p>Uses Spring's {@link org.springframework.core.MethodIntrospector} for proxy-safe method
 * discovery.
 *
 * @see SlackController
 */
public class AnnotationDrivenAppCustomizer implements SlackAppCustomizer {

  private final ApplicationContext applicationContext;

  public AnnotationDrivenAppCustomizer(ApplicationContext applicationContext) {
    this.applicationContext = applicationContext;
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
        (annotation, method, target) ->
            app.command(annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private void registerEvents(App app, Object bean) {
    scanAndRegister(
        bean,
        Event.class,
        (annotation, method, target) ->
            app.event(
                annotation.value(), (payload, ctx) -> invokeHandler(method, target, payload, ctx)));
  }

  private void registerBlockActions(App app, Object bean) {
    scanAndRegister(
        bean,
        BlockAction.class,
        (annotation, method, target) ->
            app.blockAction(
                annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private void registerBlockSuggestions(App app, Object bean) {
    scanAndRegister(
        bean,
        BlockSuggestion.class,
        (annotation, method, target) ->
            app.blockSuggestion(
                annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private void registerGlobalShortcuts(App app, Object bean) {
    scanAndRegister(
        bean,
        GlobalShortcut.class,
        (annotation, method, target) ->
            app.globalShortcut(
                annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private void registerMessageShortcuts(App app, Object bean) {
    scanAndRegister(
        bean,
        MessageShortcut.class,
        (annotation, method, target) ->
            app.messageShortcut(
                annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private void registerViewSubmissions(App app, Object bean) {
    scanAndRegister(
        bean,
        ViewSubmission.class,
        (annotation, method, target) ->
            app.viewSubmission(
                annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private void registerViewsClosed(App app, Object bean) {
    scanAndRegister(
        bean,
        ViewClosed.class,
        (annotation, method, target) ->
            app.viewClosed(
                annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private void registerMessages(App app, Object bean) {
    scanAndRegister(
        bean,
        Message.class,
        (annotation, method, target) ->
            app.message(
                Pattern.compile(annotation.value()),
                (payload, ctx) -> invokeHandler(method, target, payload, ctx)));
  }

  private void registerDialogSubmissions(App app, Object bean) {
    scanAndRegister(
        bean,
        DialogSubmission.class,
        (annotation, method, target) ->
            app.dialogSubmission(
                annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private void registerDialogSuggestions(App app, Object bean) {
    scanAndRegister(
        bean,
        DialogSuggestion.class,
        (annotation, method, target) ->
            app.dialogSuggestion(
                annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private void registerDialogCancellations(App app, Object bean) {
    scanAndRegister(
        bean,
        DialogCancellation.class,
        (annotation, method, target) ->
            app.dialogCancellation(
                annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private void registerAttachmentActions(App app, Object bean) {
    scanAndRegister(
        bean,
        AttachmentAction.class,
        (annotation, method, target) ->
            app.attachmentAction(
                annotation.value(), (req, ctx) -> invokeHandler(method, target, req, ctx)));
  }

  private <A extends Annotation> void scanAndRegister(
      Object bean, Class<A> annotationType, HandlerRegistrar<A> registrar) {
    Map<Method, A> methods =
        MethodIntrospector.selectMethods(
            bean.getClass(),
            (MethodIntrospector.MetadataLookup<A>)
                method -> AnnotatedElementUtils.findMergedAnnotation(method, annotationType));
    methods.forEach((method, annotation) -> registrar.register(annotation, method, bean));
  }

  private Response invokeHandler(Method method, Object bean, Object... args) {
    try {
      return (Response) method.invoke(bean, args);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new SlackHandlerInvocationException("Failed to invoke handler: " + method.getName(), e);
    }
  }

  @FunctionalInterface
  private interface HandlerRegistrar<A extends Annotation> {
    void register(A annotation, Method method, Object bean);
  }
}
