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
import java.util.Arrays;
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
import org.jwcarman.slack.bolt.autoconfigure.resolver.ParameterResolver;
import org.jwcarman.slack.bolt.autoconfigure.resolver.ParameterResolverFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodIntrospector;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.convert.ConversionService;

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
import com.slack.api.bolt.response.Response;

/**
 * A {@link SlackAppCustomizer} that scans {@link SlackController @SlackController} beans for
 * annotated handler methods and registers them with the Bolt {@link App}.
 *
 * <p>Uses Spring's {@link org.springframework.core.MethodIntrospector} for proxy-safe method
 * discovery. Handler method parameters are resolved using a {@link ParameterResolver} chain built
 * by {@link ParameterResolverFactory}.
 *
 * @see SlackController
 */
public class AnnotationDrivenAppCustomizer implements SlackAppCustomizer {

  private final ApplicationContext applicationContext;
  private final ConversionService conversionService;

  /**
   * Creates a new customizer that scans the given application context for controller beans.
   *
   * @param applicationContext the Spring application context used to discover controller beans
   * @param conversionService the conversion service used for parameter type coercion
   */
  public AnnotationDrivenAppCustomizer(
      ApplicationContext applicationContext, ConversionService conversionService) {
    this.applicationContext = applicationContext;
    this.conversionService = conversionService;
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
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, SlashCommandRequest.class, SlashCommandContext.class);
          app.command(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
  }

  private void registerEvents(App app, Object bean) {
    scanAndRegister(
        bean,
        Event.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, EventsApiPayload.class, EventContext.class);
          app.event(
              annotation.value(),
              (payload, ctx) -> invokeWithResolvers(method, target, resolvers, payload, ctx));
        });
  }

  private void registerBlockActions(App app, Object bean) {
    scanAndRegister(
        bean,
        BlockAction.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, BlockActionRequest.class, ActionContext.class);
          app.blockAction(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
  }

  private void registerBlockSuggestions(App app, Object bean) {
    scanAndRegister(
        bean,
        BlockSuggestion.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, BlockSuggestionRequest.class, BlockSuggestionContext.class);
          app.blockSuggestion(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
  }

  private void registerGlobalShortcuts(App app, Object bean) {
    scanAndRegister(
        bean,
        GlobalShortcut.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, GlobalShortcutRequest.class, GlobalShortcutContext.class);
          app.globalShortcut(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
  }

  private void registerMessageShortcuts(App app, Object bean) {
    scanAndRegister(
        bean,
        MessageShortcut.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, MessageShortcutRequest.class, MessageShortcutContext.class);
          app.messageShortcut(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
  }

  private void registerViewSubmissions(App app, Object bean) {
    scanAndRegister(
        bean,
        ViewSubmission.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, ViewSubmissionRequest.class, ViewSubmissionContext.class);
          app.viewSubmission(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
  }

  private void registerViewsClosed(App app, Object bean) {
    scanAndRegister(
        bean,
        ViewClosed.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, ViewClosedRequest.class, DefaultContext.class);
          app.viewClosed(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
  }

  private void registerMessages(App app, Object bean) {
    scanAndRegister(
        bean,
        Message.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, EventsApiPayload.class, EventContext.class);
          app.message(
              Pattern.compile(annotation.value()),
              (payload, ctx) -> invokeWithResolvers(method, target, resolvers, payload, ctx));
        });
  }

  private void registerDialogSubmissions(App app, Object bean) {
    scanAndRegister(
        bean,
        DialogSubmission.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, DialogSubmissionRequest.class, DialogSubmissionContext.class);
          app.dialogSubmission(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
  }

  private void registerDialogSuggestions(App app, Object bean) {
    scanAndRegister(
        bean,
        DialogSuggestion.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, DialogSuggestionRequest.class, DialogSuggestionContext.class);
          app.dialogSuggestion(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
  }

  private void registerDialogCancellations(App app, Object bean) {
    scanAndRegister(
        bean,
        DialogCancellation.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(
                  method, DialogCancellationRequest.class, DialogCancellationContext.class);
          app.dialogCancellation(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
  }

  private void registerAttachmentActions(App app, Object bean) {
    scanAndRegister(
        bean,
        AttachmentAction.class,
        (annotation, method, target) -> {
          ParameterResolver[] resolvers =
              buildResolvers(method, AttachmentActionRequest.class, AttachmentActionContext.class);
          app.attachmentAction(
              annotation.value(),
              (req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx));
        });
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

  private ParameterResolver[] buildResolvers(
      Method method, Class<?> requestType, Class<?> contextType) {
    return Arrays.stream(method.getParameters())
        .map(
            param ->
                ParameterResolverFactory.createResolver(
                    param, requestType, contextType, conversionService))
        .toArray(ParameterResolver[]::new);
  }

  private Response invokeWithResolvers(
      Method method, Object bean, ParameterResolver[] resolvers, Object req, Object ctx) {
    Object[] args = new Object[resolvers.length];
    for (int i = 0; i < resolvers.length; i++) {
      args[i] = resolvers[i].resolve(req, ctx);
    }
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
