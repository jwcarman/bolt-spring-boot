# Bolt Spring Boot Starter

[![CI](https://github.com/jwcarman/bolt-spring-boot/actions/workflows/maven.yml/badge.svg)](https://github.com/jwcarman/bolt-spring-boot/actions/workflows/maven.yml)
[![CodeQL](https://github.com/jwcarman/bolt-spring-boot/actions/workflows/github-code-scanning/codeql/badge.svg)](https://github.com/jwcarman/bolt-spring-boot/actions/workflows/github-code-scanning/codeql)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![Java](https://img.shields.io/badge/dynamic/xml?url=https://raw.githubusercontent.com/jwcarman/bolt-spring-boot/main/pom.xml&query=//*[local-name()='java.version']/text()&label=Java&color=orange)](https://openjdk.org/)
[![Maven Central](https://img.shields.io/maven-central/v/org.jwcarman.slack/bolt-spring-boot-starter)](https://central.sonatype.com/artifact/org.jwcarman.slack/bolt-spring-boot-starter)

[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=jwcarman_bolt-spring-boot&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=jwcarman_bolt-spring-boot)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=jwcarman_bolt-spring-boot&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=jwcarman_bolt-spring-boot)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=jwcarman_bolt-spring-boot&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=jwcarman_bolt-spring-boot)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=jwcarman_bolt-spring-boot&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=jwcarman_bolt-spring-boot)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=jwcarman_bolt-spring-boot&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=jwcarman_bolt-spring-boot)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=jwcarman_bolt-spring-boot&metric=coverage)](https://sonarcloud.io/summary/new_code?id=jwcarman_bolt-spring-boot)

A Spring Boot starter that provides annotation-driven handler registration for the
[Slack Bolt for Java](https://github.com/slackapi/java-slack-sdk) SDK. Build Slack apps with
Spring Boot using simple annotations instead of manual `App` configuration.

## Requirements

- Java 21+
- Spring Boot 4.x
- Slack Bolt for Java 1.48.0+

## Installation

Add the starter to your project:

### Maven

```xml
<dependency>
    <groupId>org.jwcarman.slack</groupId>
    <artifactId>bolt-spring-boot-starter</artifactId>
    <version>0.3.0</version>
</dependency>
```

### Gradle

```kotlin
implementation("org.jwcarman.slack:bolt-spring-boot-starter:0.3.0")
```

## Slack App Setup

Before using this starter, you need a Slack app. The setup differs depending on which mode you're using.

### Single-Team Mode

For internal tools or single-workspace apps:

1. **Create a Slack app** at [api.slack.com/apps](https://api.slack.com/apps) (choose "From scratch")
2. Navigate to **OAuth & Permissions** and add the bot token scopes your app needs (e.g., `chat:write`, `commands`, `app_mentions:read`)
3. Install the app to your workspace (under **Install App**) and copy the **Bot User OAuth Token** (`xoxb-...`)
4. Navigate to **Basic Information** and copy the **Signing Secret**
5. If using slash commands, register them under **Slash Commands** pointing to `https://your-domain/slack/events`
6. If using events, enable **Event Subscriptions** with the same URL and subscribe to the events you need
7. If using interactivity (actions, shortcuts, modals), enable **Interactivity & Shortcuts** with the same URL

### OAuth Mode

For apps distributed to multiple workspaces:

1. **Create a Slack app** at [api.slack.com/apps](https://api.slack.com/apps) (choose "From scratch")
2. Navigate to **OAuth & Permissions** and add the bot token scopes your app needs
3. Under **OAuth & Permissions**, add a redirect URL matching your `slack.oauth-redirect-uri-path` (default: `https://your-domain/slack/oauth_redirect`)
4. Navigate to **Basic Information** and copy the **Client ID**, **Client Secret**, and **Signing Secret**
5. If using slash commands, register them under **Slash Commands** pointing to `https://your-domain/slack/events`
6. If using events, enable **Event Subscriptions** with the same URL and subscribe to the events you need
7. If using interactivity (actions, shortcuts, modals), enable **Interactivity & Shortcuts** with the same URL

Users install your app by visiting your OAuth install URL (default: `https://your-domain/slack/install`).

For more details, see the [Slack Bolt for Java getting started guide](https://docs.slack.dev/tools/java-sdk-bolt/guides/getting-started/).

## Quick Start

### 1. Configure your Slack app

**Single-team mode** (one workspace, simple setup):

```yaml
slack:
  bot-token: ${SLACK_BOT_TOKEN}
  signing-secret: ${SLACK_SIGNING_SECRET}
```

**OAuth mode** (multi-workspace distribution):

```yaml
slack:
  client-id: ${SLACK_CLIENT_ID}
  client-secret: ${SLACK_CLIENT_SECRET}
  signing-secret: ${SLACK_SIGNING_SECRET}
  scope: app_mentions:read,channels:history,chat:write,commands
```

The starter auto-detects the mode based on which properties are present.

### 2. Create a handler

```java
@SlackController
public class MySlackHandlers {

    @SlashCommand("/hello")
    public Response hello(@UserName String name, SlashCommandContext ctx) {
        return ctx.ack("Hello, " + name + "!");
    }

    @SlashCommand("/echo")
    public String echo(@CommandText String text) {
        return "You said: " + text;
    }

    @BlockAction("approve-button")
    public void onApprove(@UserId String userId) {
        log.info("Approved by {}", userId);
    }
}
```

That's it. No `App` configuration, no servlet registration, no boilerplate. Declare only the
parameters you need, and the framework handles the rest.

## Annotations

`@SlackController` is a Spring `@Component` stereotype that marks a bean for handler scanning.
It supports an optional value for the bean name, just like `@Controller`.

The following handler annotations are available:

| Annotation | Bolt Registration | Value Type |
|---|---|---|
| `@SlashCommand` | `app.command()` | `String` (command name) |
| `@Event` | `app.event()` | `Class<? extends Event>` |
| `@BlockAction` | `app.blockAction()` | `String` (action ID) |
| `@BlockSuggestion` | `app.blockSuggestion()` | `String` (action ID) |
| `@GlobalShortcut` | `app.globalShortcut()` | `String` (callback ID) |
| `@MessageShortcut` | `app.messageShortcut()` | `String` (callback ID) |
| `@ViewSubmission` | `app.viewSubmission()` | `String` (callback ID) |
| `@ViewClosed` | `app.viewClosed()` | `String` (callback ID) |
| `@Message` | `app.message()` | `String` (regex pattern) |
| `@DialogSubmission` | `app.dialogSubmission()` | `String` (callback ID) |
| `@DialogSuggestion` | `app.dialogSuggestion()` | `String` (callback ID) |
| `@DialogCancellation` | `app.dialogCancellation()` | `String` (callback ID) |
| `@AttachmentAction` | `app.attachmentAction()` | `String` (callback ID) |

Handler methods can use [parameter injection](#parameter-injection) to declare only the values
they need, or accept the full Bolt request and context objects:

### Full Request/Context Types

| Annotation | Method Parameters |
|---|---|
| `@SlashCommand` | `(SlashCommandRequest req, SlashCommandContext ctx)` |
| `@Event` | `(EventsApiPayload<E> payload, EventContext ctx)` |
| `@BlockAction` | `(BlockActionRequest req, ActionContext ctx)` |
| `@BlockSuggestion` | `(BlockSuggestionRequest req, BlockSuggestionContext ctx)` |
| `@GlobalShortcut` | `(GlobalShortcutRequest req, GlobalShortcutContext ctx)` |
| `@MessageShortcut` | `(MessageShortcutRequest req, MessageShortcutContext ctx)` |
| `@ViewSubmission` | `(ViewSubmissionRequest req, ViewSubmissionContext ctx)` |
| `@ViewClosed` | `(ViewClosedRequest req, DefaultContext ctx)` |
| `@Message` | `(EventsApiPayload<MessageEvent> payload, EventContext ctx)` |
| `@DialogSubmission` | `(DialogSubmissionRequest req, DialogSubmissionContext ctx)` |
| `@DialogSuggestion` | `(DialogSuggestionRequest req, DialogSuggestionContext ctx)` |
| `@DialogCancellation` | `(DialogCancellationRequest req, DialogCancellationContext ctx)` |
| `@AttachmentAction` | `(AttachmentActionRequest req, ActionContext ctx)` |

All request, context, and payload types are from the `com.slack.api.bolt` package.

## Parameter Injection

Instead of accepting the full request and context objects, you can declare only the values you
need using binding annotations. The framework resolves each parameter individually, making
handler methods shorter and easier to test.

### Before and After

**Before** -- extracting values manually from the request:

```java
@SlashCommand("/greet")
public Response greet(SlashCommandRequest req, SlashCommandContext ctx) {
    String userId = req.getPayload().getUserId();
    String text = req.getPayload().getText();
    return ctx.ack("Hello <@" + userId + ">, you said: " + text);
}
```

**After** -- declaring only what you need:

```java
@SlashCommand("/greet")
public Response greet(@UserId String userId, @CommandText String text, SlashCommandContext ctx) {
    return ctx.ack("Hello <@" + userId + ">, you said: " + text);
}
```

You can also omit the request and context entirely if you do not need them:

```java
@SlashCommand("/echo")
public Response echo(@CommandText String text) {
    return new Response(200, "application/json", "{\"text\":\"" + text + "\"}");
}
```

### Universal Annotations

These annotations work with any handler type:

| Annotation | Type | Description |
|---|---|---|
| `@UserId` | `String` | The ID of the user who triggered the action |
| `@UserName` | `String` | The username of the user who triggered the action |
| `@TeamId` | `String` | The team (workspace) ID |
| `@ChannelId` | `String` | The channel ID where the action occurred |
| `@TriggerId` | `String` | The trigger ID for opening modals |
| `@ResponseUrl` | `String` | The response URL for deferred replies |

All universal annotations are in the `org.jwcarman.slack.bolt.autoconfigure.annotations.bind` package.

### Handler-Specific Annotations

These annotations extract values that are specific to a particular handler type:

| Annotation | Handler Type | Description |
|---|---|---|
| `@CommandText` | `@SlashCommand` | The text following the slash command |
| `@ActionValue` | `@BlockAction` | The value of the action element |
| `@MessageText` | `@Message` | The text of the matching message |

### Return Types

Handler methods support flexible return types:

| Return Type | Behavior |
|---|---|
| `Response` | Returned as-is to Slack |
| `String` | Wrapped in `Response.ok(text)` |
| `void` | Auto-acknowledges with `ctx.ack()` |
| Any other type | Serialized to JSON via `ctx.toJson()` and acknowledged |

### View Submission Binding with `@Block`

For `@ViewSubmission` handlers, you can bind a Java record to a block's input fields using the
`@Block` annotation. Each record component is matched to an action within the named block.

```java
public record TicketForm(String title, String description, Integer priority) {}

@ViewSubmission("create-ticket")
public void onSubmit(@Block("ticket-details") TicketForm ticket, ViewSubmissionContext ctx) {
    ticketService.create(ticket.title(), ticket.description(), ticket.priority());
}
```

If you omit the annotation value, the parameter name is used with convention-based matching.
For example, a parameter named `ticketDetails` matches blocks named `ticketDetails`,
`ticket-details`, `ticket_details`, or `TICKETDETAILS`.

Field names within the record follow the same convention-based matching against the block's
action IDs. Spring's `ConversionService` handles type coercion automatically, so a field
declared as `Integer` will be converted from the string value in the view state.

#### Overriding Action IDs with `@ActionId`

When a record field name doesn't match the action ID in the block, use `@ActionId` to specify
the exact action ID:

```java
public record ContactForm(
    @ActionId("full-name-input") String fullName,
    @ActionId("email-address-input") String email
) {}

@ViewSubmission("contact-form")
public void onSubmit(@Block ContactForm contact) {
    // contact.fullName() is bound from the "full-name-input" action
    // contact.email() is bound from the "email-address-input" action
}
```

Without `@ActionId`, the framework resolves field names using the convention-based search path.
With `@ActionId`, the value is used as an exact match -- no convention matching is attempted.

### Mixing Annotated and Raw Parameters

You can freely mix binding annotations with the raw request and context types. The framework
resolves each parameter independently:

```java
@SlashCommand("/admin")
public Response admin(@UserId String userId, SlashCommandRequest req, SlashCommandContext ctx) {
    // userId is injected; req and ctx are the raw Bolt objects
    return ctx.ack();
}
```

## Configuration Properties

All properties are under the `slack.*` prefix:

| Property | Default | Description |
|---|---|---|
| `slack.signing-secret` | | Request signing secret (required for both modes) |
| `slack.events-path` | `/slack/events` | Servlet path for events and interactions (both modes) |
| `slack.bot-token` | | Bot token (single-team mode) |
| `slack.client-id` | | OAuth client ID (OAuth mode) |
| `slack.client-secret` | | OAuth client secret (OAuth mode) |
| `slack.scope` | | Bot token scopes (OAuth mode only) |
| `slack.user-scope` | | User token scopes (OAuth mode only) |
| `slack.oauth-install-path` | `/slack/install` | OAuth install initiation path (OAuth mode only) |
| `slack.oauth-redirect-uri-path` | `/slack/oauth_redirect` | OAuth redirect callback path (OAuth mode only) |
| `slack.oauth-completion-url` | | Redirect URL after successful install (OAuth mode only) |
| `slack.oauth-cancellation-url` | | Redirect URL after cancelled install (OAuth mode only) |

**Mode detection:** If `slack.bot-token` is set, the app runs in single-team mode. Otherwise, `slack.client-id` and `slack.client-secret` are used for OAuth mode. Both modes require `slack.signing-secret`.

## Programmatic Customization

For advanced use cases, you can customize the `App` directly by providing a `SlackAppCustomizer` bean:

```java
@Bean
SlackAppCustomizer additionalSetup() {
    return app -> {
        app.command("/legacy", (req, ctx) -> ctx.ack("Still works!"));
    };
}
```

All `SlackAppCustomizer` beans are auto-discovered and applied. The annotation-driven scanner
is itself a customizer, so programmatic and annotation-driven handlers coexist naturally.

## Modules

| Module | Artifact | Purpose |
|---|---|---|
| Annotations | `bolt-spring-boot-annotations` | Annotation definitions and `@SlackController` stereotype |
| Autoconfigure | `bolt-spring-boot-autoconfigure` | Auto-configuration, handler scanning, servlet registration |
| Starter | `bolt-spring-boot-starter` | Dependency aggregator (add this to your project) |

The annotations module has minimal dependencies, so libraries that define handler interfaces
can depend on it without pulling in the full auto-configuration.

## License

[Apache License 2.0](https://www.apache.org/licenses/LICENSE-2.0)
