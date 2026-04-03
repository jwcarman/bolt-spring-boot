# Bolt Spring Boot Starter — Design Document

**Date:** 2026-04-02
**Status:** Approved

## Overview

A Spring Boot starter that provides annotation-driven handler registration for the Slack Bolt Java SDK, eliminating boilerplate configuration. Users annotate methods on Spring beans and the starter handles `App` creation, handler registration, and servlet wiring automatically.

## Coordinates & Modules

| Module | Artifact | Purpose |
|--------|----------|---------|
| Autoconfigure | `org.jwcarman.slack:bolt-spring-boot-autoconfigure` | Annotations, scanner, auto-configuration |
| Starter | `org.jwcarman.slack:bolt-spring-boot-starter` | Thin POM pulling in autoconfigure + bolt-jakarta-servlet + spring-boot-starter-web |

**Group ID:** `org.jwcarman.slack`
**Package root:** `org.jwcarman.slack.bolt.autoconfigure`

## Target Platform

- Spring Boot 4.x
- Jakarta EE
- Java 21+
- `com.slack.api:bolt-jakarta-servlet:1.48.0`
- Servlet-only (no socket mode in v1)
- OAuth (multi-workspace) as the assumed mode

## Configuration Properties

Bound to `slack.*` prefix via `SlackProperties`:

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `slack.client-id` | `String` | — | OAuth client ID |
| `slack.client-secret` | `String` | — | OAuth client secret |
| `slack.signing-secret` | `String` | — | Request signing secret |
| `slack.scope` | `String` | — | Bot scopes |
| `slack.user-scope` | `String` | — | User scopes |
| `slack.events-path` | `String` | `/slack/events` | Servlet path for events/interactions |
| `slack.oauth-install-path` | `String` | `/slack/install` | OAuth install initiation path |
| `slack.oauth-redirect-uri-path` | `String` | `/slack/oauth_redirect` | OAuth redirect callback path |
| `slack.oauth-completion-url` | `String` | — | URL to redirect after successful install |
| `slack.oauth-cancellation-url` | `String` | — | URL to redirect after cancelled install |

## Auto-Configuration

`SlackAutoConfiguration` does:

1. **Creates `AppConfig` bean** from `SlackProperties`
2. **Creates `App` bean** — marks as OAuth app, discovers all `Customizer<App>` beans from the context and applies them (supports `@Order`)
3. **Registers two servlets:**
   - `SlackAppServlet` at `slack.events-path` (POST — events/interactions)
   - `SlackOAuthAppServlet` at `slack.oauth-install-path` and `slack.oauth-redirect-uri-path` (GET — OAuth flows)

```java
@AutoConfiguration
@EnableConfigurationProperties(SlackProperties.class)
public class SlackAutoConfiguration {

    @Bean
    AppConfig appConfig(SlackProperties props) { ... }

    @Bean
    App slackApp(AppConfig config, List<Customizer<App>> customizers) {
        App app = new App(config).asOAuthApp(true);
        customizers.forEach(c -> c.customize(app));
        return app;
    }

    @Bean
    ServletRegistrationBean<SlackAppServlet> slackEventsServlet(App app, SlackProperties props) { ... }

    @Bean
    ServletRegistrationBean<SlackOAuthAppServlet> slackOAuthServlet(App app, SlackProperties props) { ... }
}
```

## Customizer Pattern

The `AnnotationDrivenAppCustomizer` is itself a `Customizer<App>` auto-configured as a bean. Users can add their own:

```java
@Bean
Customizer<App> additionalSetup() {
    return app -> {
        app.command("/legacy", (req, ctx) -> ctx.ack("still works"));
    };
}
```

All customizers are auto-discovered from the application context.

## Annotations

One annotation per Bolt handler type. Fixed method signatures (request + context params, returns `Response`).

| Annotation | Value Type | Method Params |
|---|---|---|
| `@SlashCommand` | `String` (command) | `(SlashCommandRequest, SlashCommandContext)` |
| `@Event` | `Class<? extends Event>` | `(EventsApiPayload<E>, EventContext)` |
| `@BlockAction` | `String` (actionId) | `(BlockActionRequest, ActionContext)` |
| `@BlockSuggestion` | `String` (actionId) | `(BlockSuggestionRequest, BlockSuggestionContext)` |
| `@GlobalShortcut` | `String` (callbackId) | `(GlobalShortcutRequest, GlobalShortcutContext)` |
| `@MessageShortcut` | `String` (callbackId) | `(MessageShortcutRequest, MessageShortcutContext)` |
| `@ViewSubmission` | `String` (callbackId) | `(ViewSubmissionRequest, ViewSubmissionContext)` |
| `@ViewClosed` | `String` (callbackId) | `(ViewClosedRequest, DefaultContext)` |
| `@Message` | `String` (pattern) | `(EventsApiPayload<MessageEvent>, EventContext)` |
| `@DialogSubmission` | `String` (callbackId) | `(DialogSubmissionRequest, DialogSubmissionContext)` |
| `@DialogSuggestion` | `String` (callbackId) | `(DialogSuggestionRequest, DialogSuggestionContext)` |
| `@DialogCancellation` | `String` (callbackId) | `(DialogCancellationRequest, DialogCancellationContext)` |
| `@AttachmentAction` | `String` (callbackId) | `(AttachmentActionRequest, ActionContext)` |
| `@WorkflowStepEdit` | `String` (callbackId) | `(WorkflowStepEditRequest, WorkflowStepEditContext)` |
| `@WorkflowStepSave` | `String` (callbackId) | `(WorkflowStepSaveRequest, WorkflowStepSaveContext)` |
| `@WorkflowStepExecute` | `String` (callbackId) | `(WorkflowStepExecuteRequest, WorkflowStepExecuteContext)` |

## `AnnotationDrivenAppCustomizer`

A `Customizer<App>` that:

1. Scans all Spring beans in the context
2. Finds methods annotated with any of the above annotations
3. Validates the method signature matches the expected params/return type
4. Registers each method as a handler with the `App` via the corresponding `app.command()`, `app.event()`, `app.blockAction()`, etc.

## User Experience

**`application.yml`:**
```yaml
slack:
  client-id: ${SLACK_CLIENT_ID}
  client-secret: ${SLACK_CLIENT_SECRET}
  signing-secret: ${SLACK_SIGNING_SECRET}
  scope: app_mentions:read,channels:history,chat:write,commands
```

**Handler bean:**
```java
@Component
public class MySlackHandlers {

    @SlashCommand("/deploy")
    public Response deploy(SlashCommandRequest req, SlashCommandContext ctx) {
        return ctx.ack("Deploying...");
    }

    @Event(AppMentionEvent.class)
    public Response onMention(EventsApiPayload<AppMentionEvent> event, EventContext ctx) {
        return ctx.ack();
    }

    @BlockAction("approve-button")
    public Response onApprove(BlockActionRequest req, ActionContext ctx) {
        return ctx.ack();
    }
}
```

## Project Layout

```
bolt-spring-boot/
├── settings.gradle.kts
├── bolt-spring-boot-autoconfigure/
│   ├── build.gradle.kts
│   └── src/main/java/org/jwcarman/slack/bolt/autoconfigure/
│       ├── SlackProperties.java
│       ├── SlackAutoConfiguration.java
│       ├── annotations/
│       │   ├── SlashCommand.java
│       │   ├── Event.java
│       │   ├── BlockAction.java
│       │   ├── BlockSuggestion.java
│       │   ├── GlobalShortcut.java
│       │   ├── MessageShortcut.java
│       │   ├── ViewSubmission.java
│       │   ├── ViewClosed.java
│       │   ├── Message.java
│       │   ├── DialogSubmission.java
│       │   ├── DialogSuggestion.java
│       │   ├── DialogCancellation.java
│       │   ├── AttachmentAction.java
│       │   ├── WorkflowStepEdit.java
│       │   ├── WorkflowStepSave.java
│       │   └── WorkflowStepExecute.java
│       └── registrar/
│           └── AnnotationDrivenAppCustomizer.java
├── bolt-spring-boot-starter/
│   └── build.gradle.kts
```
