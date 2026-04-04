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

### 1. Create a Slack App

The easiest way is to use the included app manifest:

1. Go to [api.slack.com/apps](https://api.slack.com/apps) and click **Create New App**
2. Choose **From an app manifest**
3. Select your workspace
4. Paste the contents of [`manifest.json`](manifest.json)
5. Review and create the app
6. Under **Install App**, install to your workspace
7. Copy the **Bot User OAuth Token** (`xoxb-...`) and **Signing Secret** (from **Basic Information**)

### 4. Run the App

```bash
export SLACK_BOT_TOKEN=xoxb-your-token
export SLACK_SIGNING_SECRET=your-signing-secret
mvn spring-boot:run -pl bolt-spring-boot-example
```

The app starts on port 3000. Use [ngrok](https://ngrok.com/) or a similar tool to expose it:

```bash
ngrok http 3000
```

Then update your Slack app's URLs with the ngrok URL.

## Try It Out

- Type `/hello` in any channel
- Type `/echo something` in any channel
- Type `/log some message` in any channel
- Mention your bot with `@YourBot` in a channel
- Type "hello" in a channel the bot is in
- Use the global shortcut "open-feedback-form" from the lightning bolt menu
