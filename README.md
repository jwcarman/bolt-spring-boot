# Bolt Spring Boot Starter

[![CI](https://github.com/jwcarman/bolt-spring-boot/actions/workflows/maven.yml/badge.svg)](https://github.com/jwcarman/bolt-spring-boot/actions/workflows/maven.yml)
[![CodeQL](https://github.com/jwcarman/bolt-spring-boot/actions/workflows/codeql.yml/badge.svg)](https://github.com/jwcarman/bolt-spring-boot/actions/workflows/codeql.yml)
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
    <version>0.1.0-SNAPSHOT</version>
</dependency>
```

### Gradle

```kotlin
implementation("org.jwcarman.slack:bolt-spring-boot-starter:0.1.0-SNAPSHOT")
```

## Quick Start

### 1. Configure your Slack app

```yaml
slack:
  client-id: ${SLACK_CLIENT_ID}
  client-secret: ${SLACK_CLIENT_SECRET}
  signing-secret: ${SLACK_SIGNING_SECRET}
  scope: app_mentions:read,channels:history,chat:write,commands
```

### 2. Create a handler

```java
@SlackController
public class MySlackHandlers {

    @SlashCommand("/hello")
    public Response hello(SlashCommandRequest req, SlashCommandContext ctx) {
        return ctx.ack("Hello, " + req.getPayload().getUserName() + "!");
    }

    @Event(AppMentionEvent.class)
    public Response onMention(EventsApiPayload<AppMentionEvent> event, EventContext ctx) {
        ctx.say("You mentioned me!");
        return ctx.ack();
    }

    @BlockAction("approve-button")
    public Response onApprove(BlockActionRequest req, ActionContext ctx) {
        return ctx.ack();
    }
}
```

That's it. No `App` configuration, no servlet registration, no boilerplate.

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

Each annotated method must match the corresponding Bolt handler signature (request/payload + context)
and return `Response`.

## Configuration Properties

All properties are under the `slack.*` prefix:

| Property | Default | Description |
|---|---|---|
| `slack.client-id` | | OAuth client ID (required) |
| `slack.client-secret` | | OAuth client secret (required) |
| `slack.signing-secret` | | Request signing secret (required) |
| `slack.scope` | | Bot token scopes |
| `slack.user-scope` | | User token scopes |
| `slack.events-path` | `/slack/events` | Servlet path for events and interactions |
| `slack.oauth-install-path` | `/slack/install` | OAuth install initiation path |
| `slack.oauth-redirect-uri-path` | `/slack/oauth_redirect` | OAuth redirect callback path |
| `slack.oauth-completion-url` | | Redirect URL after successful install |
| `slack.oauth-cancellation-url` | | Redirect URL after cancelled install |

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
