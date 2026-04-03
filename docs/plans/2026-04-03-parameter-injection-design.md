# Parameter Injection — Design Document

**Date:** 2026-04-03
**Status:** Approved
**Target Version:** 0.3.0

## Overview

Add flexible parameter injection to handler methods, allowing users to declare only the
parameters they need instead of the fixed `(Request, Context)` signature. Modeled after
Python Bolt's argument injection and Spring MVC's parameter resolution.

## Before/After

**Before (v0.2.x — fixed signatures):**
```java
@SlashCommand("/deploy")
public Response deploy(SlashCommandRequest req, SlashCommandContext ctx) {
    String text = req.getPayload().getText();
    String userId = req.getPayload().getUserId();
    ctx.say("Deploying " + text + " for " + userId);
    return ctx.ack();
}
```

**After (v0.3.0 — flexible parameters):**
```java
@SlashCommand("/deploy")
public Response deploy(@CommandText String text, @UserId String userId, SlashCommandContext ctx) {
    ctx.say("Deploying " + text + " for " + userId);
    return ctx.ack();
}
```

## Binding Annotations

All in `bolt-spring-boot-annotations`, `@Target(ElementType.PARAMETER)`, `@Retention(RetentionPolicy.RUNTIME)`.

### Universal (any handler type that has the data)

| Annotation | Description |
|---|---|
| `@UserId` | The user who triggered the action |
| `@UserName` | The user's display name |
| `@TeamId` | The workspace/team ID |
| `@ChannelId` | The channel ID |
| `@TriggerId` | Trigger ID for opening modals |
| `@ResponseUrl` | The response URL for delayed responses |

### Handler-Specific

| Annotation | Handler Types | Description |
|---|---|---|
| `@CommandText` | `@SlashCommand` | Text after the command |
| `@ActionValue` | `@BlockAction` | Selected/submitted value |
| `@MessageText` | `@Event`, `@Message` | The message text |

### View State Binding

| Annotation | Handler Types | Description |
|---|---|---|
| `@Block` | `@ViewSubmission` | Binds a POJO/record to a block's fields |

`@Block` takes an optional `value()` to override the block name. Without it, the parameter
name is used with convention-based matching (exact → kebab-case → snake_case → case-insensitive).

## Parameter Resolution

### ParameterResolver Interface

```java
@FunctionalInterface
public interface ParameterResolver {
    Object resolve(Object request, Object context);
}
```

### Resolution Order

For each method parameter, the framework selects a resolver:

1. Has a binding annotation → corresponding resolver (e.g., `UserIdResolver`)
2. Type matches handler's request type → inject raw request
3. Type matches handler's context type → inject raw context
4. No match → fail at startup with clear error

### Resolver Implementation Pattern

Each resolver uses pattern matching switch on the request type:

```java
public class UserIdResolver {
    public static ParameterResolver create() {
        return (req, ctx) -> switch (req) {
            case SlashCommandRequest r -> r.getPayload().getUserId();
            case BlockActionRequest r -> r.getPayload().getUser().getId();
            case ViewSubmissionRequest r -> r.getPayload().getUser().getId();
            // etc.
            default -> throw new IllegalArgumentException(
                "Cannot extract user ID from " + req.getClass().getSimpleName());
        };
    }
}
```

### ParameterResolverFactory

Selects the right resolver for a parameter based on its annotations and type:

```java
public class ParameterResolverFactory {
    public ParameterResolver createResolver(
            Parameter parameter,
            Class<?> requestType,
            Class<?> contextType,
            ConversionService conversionService) {
        // Check binding annotations first
        // Then check raw type injection
        // Then fail
    }
}
```

## Three Utilities

### ValueExtractor

Extracts the raw value from `ViewState.Value` based on its `type` field:

```java
public class ValueExtractor {
    public static Object extract(ViewState.Value value) {
        return switch (value.getType()) {
            case "plain_text_input"     -> value.getValue();
            case "datepicker"           -> value.getSelectedDate();
            case "timepicker"           -> value.getSelectedTime();
            case "static_select"        -> value.getSelectedOption().getValue();
            case "multi_static_select"  -> selectedValues(value);
            case "users_select"         -> value.getSelectedUser();
            case "multi_users_select"   -> value.getSelectedUsers();
            case "conversations_select" -> value.getSelectedConversation();
            case "channels_select"      -> value.getSelectedChannel();
            case "rich_text_input"      -> value.getRichTextValue();
            case "file_input"           -> value.getFiles();
            default -> throw new IllegalArgumentException(
                "Unknown input type: " + value.getType());
        };
    }
}
```

### NameResolver

Finds a matching key from a set of candidates using a search path:

```java
public class NameResolver {
    public static Optional<String> resolve(String javaName, Set<String> candidates) {
        // 1. exact:            "firstName"
        // 2. kebab-case:       "first-name"
        // 3. snake_case:       "first_name"
        // 4. case-insensitive: "firstname" matches "FirstName", "FIRSTNAME", etc.
    }
}
```

### ValueConverter

Delegates to Spring's ConversionService for type coercion:

```java
public class ValueConverter {
    public static Object convert(
            Object rawValue, Class<?> targetType, ConversionService conversionService) {
        if (targetType.isInstance(rawValue)) return rawValue;
        return conversionService.convert(rawValue, targetType);
    }
}
```

## @Block Binding Flow

For a `@ViewSubmission` handler with `@Block CreateTicketForm form`:

1. **Resolve block name**: parameter name `form` (or `@Block("create-ticket")` override)
2. **Find block in view state**: `NameResolver.resolve("form", viewState.getValues().keySet())`
3. **Get block's field map**: `Map<String, ViewState.Value>`
4. **For each record component** (or POJO field):
   a. `NameResolver.resolve(fieldName, fieldMap.keySet())` → find matching action ID
   b. `ValueExtractor.extract(value)` → get raw value from the input type
   c. `ValueConverter.convert(rawValue, fieldType, conversionService)` → coerce to target type
5. **Construct the record/POJO** via canonical constructor or setters

## AnnotationDrivenAppCustomizer Changes

The `customize()` method changes from directly registering `(req, ctx) -> invokeHandler(method, bean, req, ctx)` to:

1. Build `ParameterResolver[]` for the method
2. Register `(req, ctx) -> invokeWithResolvers(method, bean, resolvers, req, ctx)`

The `invokeWithResolvers` method:
1. Iterates resolvers to build `Object[] args`
2. Calls `method.invoke(bean, args)`
3. Wraps exceptions in `SlackHandlerInvocationException`

## Backward Compatibility

Fully backward compatible. The fixed `(Request, Context)` signature still works — the
framework resolves those parameters via type matching (items 2 and 3 in the resolution order).

## Module Layout (new files only)

```
bolt-spring-boot-annotations/
  annotations/
    UserId.java
    UserName.java
    TeamId.java
    ChannelId.java
    TriggerId.java
    ResponseUrl.java
    CommandText.java
    ActionValue.java
    MessageText.java
    Block.java

bolt-spring-boot-autoconfigure/
  resolver/
    ParameterResolver.java
    ParameterResolverFactory.java
    UserIdResolver.java
    UserNameResolver.java
    TeamIdResolver.java
    ChannelIdResolver.java
    TriggerIdResolver.java
    ResponseUrlResolver.java
    CommandTextResolver.java
    ActionValueResolver.java
    MessageTextResolver.java
    BlockParameterResolver.java
    RequestParameterResolver.java
    ContextParameterResolver.java
  ValueExtractor.java
  ValueConverter.java
  NameResolver.java
```
