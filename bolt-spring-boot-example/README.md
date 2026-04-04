# Bolt Spring Boot Example

A working Slack app that demonstrates all features of the Bolt Spring Boot starter.

## Features Demonstrated

| Feature | Handler | File |
|---|---|---|
| `@SlashCommand` with `String` return | `/hello` | `GreetingHandlers` |
| `@SlashCommand` with `void` return (auto-ack) | `/log` | `GreetingHandlers` |
| `@SlashCommand` with `Response` return | `/echo` | `GreetingHandlers` |
| `@BlockAction` with `@ActionValue` | `feedback-rating` | `InteractiveHandlers` |
| `@BlockAction` with object return (JSON) | `get-status` | `InteractiveHandlers` |
| `@GlobalShortcut` opening a modal | `open-feedback-form` | `InteractiveHandlers` |
| `@ViewSubmission` with `@Block` record | `submit-feedback` | `InteractiveHandlers` |
| `@Message` pattern matching | `(?i)hello` | `InteractiveHandlers` |
| `@Event` with raw payload | `AppMentionEvent` | `EventHandlers` |
| Programmatic `SlackAppCustomizer` | logging | `CustomizerConfig` |
| Parameter injection | `@UserId`, `@UserName`, `@CommandText`, `@MessageText`, `@TriggerId`, `@ActionValue` | various |

## Setup

### 1. Start ngrok

You need a public URL for Slack to reach your local app. Start [ngrok](https://ngrok.com/):

```bash
ngrok http 8080
```

Note the forwarding URL (e.g., `https://xxxx-xx-xx-xxx-xxx.ngrok-free.app`).

### 2. Create a Slack App

1. Open [`manifest.json`](manifest.json) and replace all instances of `https://your-domain` with your ngrok URL
2. Go to [api.slack.com/apps](https://api.slack.com/apps) and click **Create New App**
3. Choose **From an app manifest**
4. Select your workspace
5. Paste the updated manifest JSON
6. Review and create the app
7. Under **Install App**, install to your workspace
8. Copy the **Bot User OAuth Token** (`xoxb-...`) from **OAuth & Permissions**
9. Copy the **Signing Secret** from **Basic Information**

### 3. Run the App

```bash
export SLACK_BOT_TOKEN=xoxb-your-token
export SLACK_SIGNING_SECRET=your-signing-secret
mvn spring-boot:run -pl bolt-spring-boot-example
```

The app starts on port 8080.

## Try It Out

### Slash Commands

- `/hello` — returns a greeting with your username (String return type)
- `/echo some text` — echoes your text back (Response return type)
- `/log some message` — logs your message server-side (void return, auto-ack)

### Global Shortcut + Modal

1. Click the lightning bolt icon in the message composer
2. Search for **Open Feedback Form**
3. Click it — a modal opens
4. Type feedback and click **Submit**
5. Check the app logs — your feedback is bound to a `FeedbackForm` record via `@Block`

### Events

- Invite the bot to a channel, then mention it: `@Bolt Example hey there`
- Type "hello" in a channel the bot is in — the `@Message` handler responds

## Unit Testing

The handlers are plain methods that take simple types, making them trivially testable
without any Slack infrastructure. See `GreetingHandlersTest` and `InteractiveHandlersTest`
for examples:

```java
@Test
void helloReturnsGreeting() {
    var result = handlers.hello("James");
    assertThat(result).isEqualTo("Hello, James!");
}

@Test
void onFeedbackSubmitDoesNotThrow() {
    var feedback = new FeedbackForm("Great framework!");
    handlers.onFeedbackSubmit("U456", feedback);
}
```
