# Parameter Injection Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Add flexible parameter injection to handler methods so users can declare only the parameters they need, with convention-based binding and type coercion.

**Architecture:** A `ParameterResolver` functional interface with implementations for each binding annotation. At startup, the `AnnotationDrivenAppCustomizer` builds a resolver array per handler method. At request time, resolvers extract values and the method is invoked with the resolved args. Three core utilities (`NameResolver`, `ValueExtractor`, `ValueConverter`) support `@Block` binding for view state.

**Tech Stack:** Java 21, Spring Boot 4.0.5, Spring ConversionService, com.slack.api:bolt:1.48.0

---

### Task 1: NameResolver

The utility that finds matching keys from a set of candidates using a search path.

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/resolver/NameResolverTest.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/resolver/NameResolver.java`

**Step 1: Write the failing test**

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

class NameResolverTest {

  @Test
  void resolvesExactMatch() {
    Set<String> candidates = Set.of("firstName", "last-name");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("firstName"));
  }

  @Test
  void resolvesKebabCase() {
    Set<String> candidates = Set.of("first-name", "last-name");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("first-name"));
  }

  @Test
  void resolvesSnakeCase() {
    Set<String> candidates = Set.of("first_name", "last_name");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("first_name"));
  }

  @Test
  void resolvesCaseInsensitive() {
    Set<String> candidates = Set.of("FIRSTNAME", "LASTNAME");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("FIRSTNAME"));
  }

  @Test
  void returnsEmptyWhenNoMatch() {
    Set<String> candidates = Set.of("unrelated", "other");
    assertThat(NameResolver.resolve("firstName", candidates)).isEmpty();
  }

  @Test
  void prefersExactOverKebab() {
    Set<String> candidates = Set.of("firstName", "first-name");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("firstName"));
  }

  @Test
  void prefersKebabOverSnake() {
    Set<String> candidates = Set.of("first-name", "first_name");
    assertThat(NameResolver.resolve("firstName", candidates)).isEqualTo(Optional.of("first-name"));
  }

  @Test
  void handlesSingleWordName() {
    Set<String> candidates = Set.of("title");
    assertThat(NameResolver.resolve("title", candidates)).isEqualTo(Optional.of("title"));
  }
}
```

**Step 2: Run test to verify it fails**

Run: `mvn clean test -pl bolt-spring-boot-autoconfigure -Dtest='NameResolverTest'`
Expected: FAIL — `NameResolver` does not exist

**Step 3: Write minimal implementation**

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import java.util.Optional;
import java.util.Set;

public class NameResolver {

  public static Optional<String> resolve(String javaName, Set<String> candidates) {
    // 1. Exact match
    if (candidates.contains(javaName)) {
      return Optional.of(javaName);
    }

    // 2. Kebab-case
    String kebab = toKebabCase(javaName);
    if (candidates.contains(kebab)) {
      return Optional.of(kebab);
    }

    // 3. Snake_case
    String snake = toSnakeCase(javaName);
    if (candidates.contains(snake)) {
      return Optional.of(snake);
    }

    // 4. Case-insensitive
    String lowerName = javaName.toLowerCase();
    for (String candidate : candidates) {
      if (candidate.toLowerCase().equals(lowerName)) {
        return Optional.of(candidate);
      }
    }

    return Optional.empty();
  }

  private static String toKebabCase(String javaName) {
    return javaName.replaceAll("([a-z])([A-Z])", "$1-$2").toLowerCase();
  }

  private static String toSnakeCase(String javaName) {
    return javaName.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
  }
}
```

**Step 4: Run test to verify it passes**

Run: `mvn clean test -pl bolt-spring-boot-autoconfigure -Dtest='NameResolverTest'`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: add NameResolver for convention-based name matching"
```

---

### Task 2: ValueExtractor

Extracts the raw value from `ViewState.Value` based on its `type` field.

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/resolver/ValueExtractorTest.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/resolver/ValueExtractor.java`

**Step 1: Write the failing test**

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.slack.api.model.view.ViewState;

class ValueExtractorTest {

  @Test
  void extractsPlainTextInput() {
    ViewState.Value value = new ViewState.Value();
    value.setType("plain_text_input");
    value.setValue("hello");
    assertThat(ValueExtractor.extract(value)).isEqualTo("hello");
  }

  @Test
  void extractsDatePicker() {
    ViewState.Value value = new ViewState.Value();
    value.setType("datepicker");
    value.setSelectedDate("2026-04-03");
    assertThat(ValueExtractor.extract(value)).isEqualTo("2026-04-03");
  }

  @Test
  void extractsTimePicker() {
    ViewState.Value value = new ViewState.Value();
    value.setType("timepicker");
    value.setSelectedTime("14:30");
    assertThat(ValueExtractor.extract(value)).isEqualTo("14:30");
  }

  @Test
  void extractsStaticSelect() {
    ViewState.SelectedOption option = new ViewState.SelectedOption();
    option.setValue("high");
    ViewState.Value value = new ViewState.Value();
    value.setType("static_select");
    value.setSelectedOption(option);
    assertThat(ValueExtractor.extract(value)).isEqualTo("high");
  }

  @Test
  void extractsMultiStaticSelect() {
    ViewState.SelectedOption opt1 = new ViewState.SelectedOption();
    opt1.setValue("a");
    ViewState.SelectedOption opt2 = new ViewState.SelectedOption();
    opt2.setValue("b");
    ViewState.Value value = new ViewState.Value();
    value.setType("multi_static_select");
    value.setSelectedOptions(List.of(opt1, opt2));
    assertThat(ValueExtractor.extract(value)).isEqualTo(List.of("a", "b"));
  }

  @Test
  void extractsUsersSelect() {
    ViewState.Value value = new ViewState.Value();
    value.setType("users_select");
    value.setSelectedUser("U12345");
    assertThat(ValueExtractor.extract(value)).isEqualTo("U12345");
  }

  @Test
  void extractsMultiUsersSelect() {
    ViewState.Value value = new ViewState.Value();
    value.setType("multi_users_select");
    value.setSelectedUsers(List.of("U1", "U2"));
    assertThat(ValueExtractor.extract(value)).isEqualTo(List.of("U1", "U2"));
  }

  @Test
  void extractsConversationsSelect() {
    ViewState.Value value = new ViewState.Value();
    value.setType("conversations_select");
    value.setSelectedConversation("C12345");
    assertThat(ValueExtractor.extract(value)).isEqualTo("C12345");
  }

  @Test
  void extractsChannelsSelect() {
    ViewState.Value value = new ViewState.Value();
    value.setType("channels_select");
    value.setSelectedChannel("C12345");
    assertThat(ValueExtractor.extract(value)).isEqualTo("C12345");
  }

  @Test
  void throwsForUnknownType() {
    ViewState.Value value = new ViewState.Value();
    value.setType("unknown_widget");
    assertThatThrownBy(() -> ValueExtractor.extract(value))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("unknown_widget");
  }
}
```

**Step 2: Run test to verify it fails**

Run: `mvn clean test -pl bolt-spring-boot-autoconfigure -Dtest='ValueExtractorTest'`
Expected: FAIL — `ValueExtractor` does not exist

**Step 3: Write minimal implementation**

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import com.slack.api.model.view.ViewState;

public class ValueExtractor {

  public static Object extract(ViewState.Value value) {
    return switch (value.getType()) {
      case "plain_text_input" -> value.getValue();
      case "datepicker" -> value.getSelectedDate();
      case "timepicker" -> value.getSelectedTime();
      case "static_select" -> value.getSelectedOption().getValue();
      case "multi_static_select" ->
          value.getSelectedOptions().stream()
              .map(ViewState.SelectedOption::getValue)
              .toList();
      case "users_select" -> value.getSelectedUser();
      case "multi_users_select" -> value.getSelectedUsers();
      case "conversations_select" -> value.getSelectedConversation();
      case "channels_select" -> value.getSelectedChannel();
      case "rich_text_input" -> value.getRichTextValue();
      case "file_input" -> value.getFiles();
      default ->
          throw new IllegalArgumentException("Unknown input type: " + value.getType());
    };
  }
}
```

**Step 4: Run test to verify it passes**

Run: `mvn clean test -pl bolt-spring-boot-autoconfigure -Dtest='ValueExtractorTest'`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: add ValueExtractor for ViewState.Value type dispatch"
```

---

### Task 3: ValueConverter

Wraps Spring's ConversionService for type coercion.

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/resolver/ValueConverterTest.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/resolver/ValueConverter.java`

**Step 1: Write the failing test**

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

class ValueConverterTest {

  private final DefaultConversionService conversionService = new DefaultConversionService();

  @Test
  void returnsValueWhenAlreadyCorrectType() {
    assertThat(ValueConverter.convert("hello", String.class, conversionService))
        .isEqualTo("hello");
  }

  @Test
  void convertsStringToInteger() {
    assertThat(ValueConverter.convert("42", Integer.class, conversionService))
        .isEqualTo(42);
  }

  @Test
  void convertsStringToLong() {
    assertThat(ValueConverter.convert("123456789", Long.class, conversionService))
        .isEqualTo(123456789L);
  }

  @Test
  void convertsStringToBoolean() {
    assertThat(ValueConverter.convert("true", Boolean.class, conversionService))
        .isEqualTo(true);
  }
}
```

**Step 2: Run test to verify it fails**

Run: `mvn clean test -pl bolt-spring-boot-autoconfigure -Dtest='ValueConverterTest'`
Expected: FAIL — `ValueConverter` does not exist

**Step 3: Write minimal implementation**

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import org.springframework.core.convert.ConversionService;

public class ValueConverter {

  public static Object convert(Object rawValue, Class<?> targetType,
      ConversionService conversionService) {
    if (targetType.isInstance(rawValue)) {
      return rawValue;
    }
    return conversionService.convert(rawValue, targetType);
  }
}
```

**Step 4: Run test to verify it passes**

Run: `mvn clean test -pl bolt-spring-boot-autoconfigure -Dtest='ValueConverterTest'`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: add ValueConverter wrapping Spring ConversionService"
```

---

### Task 4: Binding Annotations

Create all parameter-level binding annotations in the annotations module.

**Files:**
- Create: `bolt-spring-boot-annotations/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/bind/UserId.java`
- Create: `bolt-spring-boot-annotations/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/bind/UserName.java`
- Create: `bolt-spring-boot-annotations/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/bind/TeamId.java`
- Create: `bolt-spring-boot-annotations/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/bind/ChannelId.java`
- Create: `bolt-spring-boot-annotations/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/bind/TriggerId.java`
- Create: `bolt-spring-boot-annotations/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/bind/ResponseUrl.java`
- Create: `bolt-spring-boot-annotations/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/bind/CommandText.java`
- Create: `bolt-spring-boot-annotations/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/bind/ActionValue.java`
- Create: `bolt-spring-boot-annotations/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/bind/MessageText.java`
- Create: `bolt-spring-boot-annotations/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/bind/Block.java`

**Step 1: Create all annotations**

Each follows this template (all are `@Target(ElementType.PARAMETER)`, `@Retention(RetentionPolicy.RUNTIME)`):

```java
package org.jwcarman.slack.bolt.autoconfigure.annotations.bind;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface UserId {}
```

`@Block` is the only one with an optional value:

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Block {
    String value() default "";
}
```

**Step 2: Verify build**

Run: `mvn clean verify`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add .
git commit -m "feat: add parameter binding annotations in annotations.bind package"
```

---

### Task 5: ParameterResolver Interface and Simple Resolvers

Create the ParameterResolver functional interface and the request/context type resolvers.

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/resolver/ParameterResolver.java`
- Create: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/resolver/UserIdResolverTest.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/resolver/UserIdResolver.java`

**Step 1: Create the interface**

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

@FunctionalInterface
public interface ParameterResolver {
    Object resolve(Object request, Object context);
}
```

**Step 2: Write the failing test for UserIdResolver**

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

import com.slack.api.app_backend.interactive_components.payload.BlockActionPayload;
import com.slack.api.app_backend.slash_commands.payload.SlashCommandPayload;
import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;

class UserIdResolverTest {

  private final ParameterResolver resolver = UserIdResolver.create();

  @Test
  void extractsFromSlashCommand() {
    SlashCommandRequest req = mock(SlashCommandRequest.class);
    SlashCommandPayload payload = mock(SlashCommandPayload.class);
    when(req.getPayload()).thenReturn(payload);
    when(payload.getUserId()).thenReturn("U12345");

    assertThat(resolver.resolve(req, null)).isEqualTo("U12345");
  }

  @Test
  void extractsFromBlockAction() {
    BlockActionRequest req = mock(BlockActionRequest.class);
    BlockActionPayload payload = mock(BlockActionPayload.class);
    BlockActionPayload.User user = new BlockActionPayload.User();
    user.setId("U67890");
    when(req.getPayload()).thenReturn(payload);
    when(payload.getUser()).thenReturn(user);

    assertThat(resolver.resolve(req, null)).isEqualTo("U67890");
  }
}
```

**Step 3: Run test to verify it fails**

Run: `mvn clean test -pl bolt-spring-boot-autoconfigure -Dtest='UserIdResolverTest'`
Expected: FAIL — `UserIdResolver` does not exist

**Step 4: Write the implementation**

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import com.slack.api.bolt.request.builtin.BlockActionRequest;
import com.slack.api.bolt.request.builtin.GlobalShortcutRequest;
import com.slack.api.bolt.request.builtin.MessageShortcutRequest;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;

public class UserIdResolver {

  public static ParameterResolver create() {
    return (req, ctx) -> switch (req) {
      case SlashCommandRequest r -> r.getPayload().getUserId();
      case BlockActionRequest r -> r.getPayload().getUser().getId();
      case ViewSubmissionRequest r -> r.getPayload().getUser().getId();
      case GlobalShortcutRequest r -> r.getPayload().getUser().getId();
      case MessageShortcutRequest r -> r.getPayload().getUser().getId();
      default -> throw new IllegalArgumentException(
          "@UserId not supported for " + req.getClass().getSimpleName());
    };
  }
}
```

**Step 5: Run test to verify it passes**

Run: `mvn clean test -pl bolt-spring-boot-autoconfigure -Dtest='UserIdResolverTest'`
Expected: PASS

**Step 6: Commit**

```bash
git add .
git commit -m "feat: add ParameterResolver interface and UserIdResolver"
```

---

### Task 6: Remaining Universal Resolvers

Create resolver classes for all remaining universal binding annotations: UserName, TeamId, ChannelId, TriggerId, ResponseUrl. Each follows the same pattern as UserIdResolver — a static `create()` factory method returning a `ParameterResolver` with a pattern-matching switch.

**Files (per resolver — create + test for each):**
- `UserNameResolver.java` / `UserNameResolverTest.java`
- `TeamIdResolver.java` / `TeamIdResolverTest.java`
- `ChannelIdResolver.java` / `ChannelIdResolverTest.java`
- `TriggerIdResolver.java` / `TriggerIdResolverTest.java`
- `ResponseUrlResolver.java` / `ResponseUrlResolverTest.java`

**Extraction paths (from SDK analysis):**

| Resolver | SlashCommand | BlockAction | ViewSubmission | GlobalShortcut | MessageShortcut |
|---|---|---|---|---|---|
| UserName | `payload.getUserName()` | `payload.getUser().getUsername()` | — | — | — |
| TeamId | `payload.getTeamId()` | `payload.getTeam().getId()` | `payload.getTeam().getId()` | `payload.getTeam().getId()` | `payload.getTeam().getId()` |
| ChannelId | `payload.getChannelId()` | `payload.getChannel().getId()` | — | — | `payload.getChannel().getId()` |
| TriggerId | `payload.getTriggerId()` | `payload.getTriggerId()` | `payload.getTriggerId()` | `payload.getTriggerId()` | `payload.getTriggerId()` |
| ResponseUrl | `payload.getResponseUrl()` | `payload.getResponseUrl()` | — | — | `payload.getResponseUrl()` |

Where `—` means not available — the switch `default` branch throws.

**TDD for each:** Write test with at least 2 request types, verify fails, implement, verify passes.

**Step 1-5 per resolver:** Follow the same TDD cycle as Task 5.

**Step 6: Commit all resolvers**

```bash
git add .
git commit -m "feat: add universal parameter resolvers (UserName, TeamId, ChannelId, TriggerId, ResponseUrl)"
```

---

### Task 7: Handler-Specific Resolvers

Create resolvers for CommandText, ActionValue, and MessageText.

**Files:**
- Create: `CommandTextResolver.java` / `CommandTextResolverTest.java`
- Create: `ActionValueResolver.java` / `ActionValueResolverTest.java`
- Create: `MessageTextResolver.java` / `MessageTextResolverTest.java`

**Extraction paths:**

| Resolver | Request Type | Path |
|---|---|---|
| CommandText | SlashCommandRequest | `payload.getText()` |
| ActionValue | BlockActionRequest | `payload.getActions().get(0).getValue()` |
| MessageText | EventsApiPayload (MessageEvent) | `payload.getEvent().getText()` |
| MessageText | EventsApiPayload (AppMentionEvent) | `payload.getEvent().getText()` |

These only apply to their specific handler types — the `default` branch throws for any other request type.

**TDD for each.** Same cycle.

**Commit:**

```bash
git add .
git commit -m "feat: add handler-specific resolvers (CommandText, ActionValue, MessageText)"
```

---

### Task 8: BlockParameterResolver

The complex resolver that binds a POJO/record from view state using NameResolver, ValueExtractor, and ValueConverter.

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/resolver/BlockParameterResolverTest.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/resolver/BlockParameterResolver.java`

**Step 1: Write the failing test**

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.core.convert.support.DefaultConversionService;

import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.app_backend.views.payload.ViewSubmissionPayload;
import com.slack.api.model.view.View;
import com.slack.api.model.view.ViewState;

class BlockParameterResolverTest {

  public record SimpleForm(String title, String description) {}

  @Test
  void bindsRecordFromViewState() {
    ViewState.Value titleValue = new ViewState.Value();
    titleValue.setType("plain_text_input");
    titleValue.setValue("My Title");

    ViewState.Value descValue = new ViewState.Value();
    descValue.setType("plain_text_input");
    descValue.setValue("My Description");

    Map<String, Map<String, ViewState.Value>> stateValues = Map.of(
        "simple-form", Map.of(
            "title", titleValue,
            "description", descValue));

    ViewState state = new ViewState();
    state.setValues(stateValues);
    View view = new View();
    view.setState(state);
    ViewSubmissionPayload payload = mock(ViewSubmissionPayload.class);
    when(payload.getView()).thenReturn(view);
    ViewSubmissionRequest req = mock(ViewSubmissionRequest.class);
    when(req.getPayload()).thenReturn(payload);

    ParameterResolver resolver = BlockParameterResolver.create(
        "simpleForm", SimpleForm.class, new DefaultConversionService());

    SimpleForm form = (SimpleForm) resolver.resolve(req, null);
    assertThat(form.title()).isEqualTo("My Title");
    assertThat(form.description()).isEqualTo("My Description");
  }

  @Test
  void bindsWithExplicitBlockName() {
    ViewState.Value titleValue = new ViewState.Value();
    titleValue.setType("plain_text_input");
    titleValue.setValue("Explicit");

    Map<String, Map<String, ViewState.Value>> stateValues = Map.of(
        "my-custom-block", Map.of("title", titleValue));

    ViewState state = new ViewState();
    state.setValues(stateValues);
    View view = new View();
    view.setState(state);
    ViewSubmissionPayload payload = mock(ViewSubmissionPayload.class);
    when(payload.getView()).thenReturn(view);
    ViewSubmissionRequest req = mock(ViewSubmissionRequest.class);
    when(req.getPayload()).thenReturn(payload);

    ParameterResolver resolver = BlockParameterResolver.create(
        "my-custom-block", SimpleForm.class, new DefaultConversionService());

    SimpleForm form = (SimpleForm) resolver.resolve(req, null);
    assertThat(form.title()).isEqualTo("Explicit");
  }

  public record TypedForm(String name, Integer age) {}

  @Test
  void convertsTypesViaConversionService() {
    ViewState.Value nameValue = new ViewState.Value();
    nameValue.setType("plain_text_input");
    nameValue.setValue("James");

    ViewState.Value ageValue = new ViewState.Value();
    ageValue.setType("plain_text_input");
    ageValue.setValue("42");

    Map<String, Map<String, ViewState.Value>> stateValues = Map.of(
        "typed-form", Map.of("name", nameValue, "age", ageValue));

    ViewState state = new ViewState();
    state.setValues(stateValues);
    View view = new View();
    view.setState(state);
    ViewSubmissionPayload payload = mock(ViewSubmissionPayload.class);
    when(payload.getView()).thenReturn(view);
    ViewSubmissionRequest req = mock(ViewSubmissionRequest.class);
    when(req.getPayload()).thenReturn(payload);

    ParameterResolver resolver = BlockParameterResolver.create(
        "typedForm", TypedForm.class, new DefaultConversionService());

    TypedForm form = (TypedForm) resolver.resolve(req, null);
    assertThat(form.name()).isEqualTo("James");
    assertThat(form.age()).isEqualTo(42);
  }
}
```

**Step 2: Run test to verify it fails**

Run: `mvn clean test -pl bolt-spring-boot-autoconfigure -Dtest='BlockParameterResolverTest'`
Expected: FAIL — `BlockParameterResolver` does not exist

**Step 3: Write the implementation**

The `BlockParameterResolver.create()` method returns a `ParameterResolver` that:
1. Extracts view state from `ViewSubmissionRequest`
2. Uses `NameResolver.resolve()` to find the block in the state map
3. For each record component, uses `NameResolver.resolve()` to find the action ID
4. Uses `ValueExtractor.extract()` to get the raw value
5. Uses `ValueConverter.convert()` to coerce to the target type
6. Constructs the record via its canonical constructor

```java
package org.jwcarman.slack.bolt.autoconfigure.resolver;

import java.lang.reflect.RecordComponent;
import java.util.Map;

import org.springframework.core.convert.ConversionService;

import com.slack.api.bolt.request.builtin.ViewSubmissionRequest;
import com.slack.api.model.view.ViewState;

public class BlockParameterResolver {

  public static ParameterResolver create(
      String blockName, Class<?> recordType, ConversionService conversionService) {
    return (req, ctx) -> {
      ViewSubmissionRequest viewReq = (ViewSubmissionRequest) req;
      Map<String, Map<String, ViewState.Value>> stateValues =
          viewReq.getPayload().getView().getState().getValues();

      String resolvedBlock = NameResolver.resolve(blockName, stateValues.keySet())
          .orElseThrow(() -> new IllegalArgumentException(
              "Block '" + blockName + "' not found in view state"));

      Map<String, ViewState.Value> blockValues = stateValues.get(resolvedBlock);
      RecordComponent[] components = recordType.getRecordComponents();
      Object[] args = new Object[components.length];

      for (int i = 0; i < components.length; i++) {
        RecordComponent component = components[i];
        String fieldKey = NameResolver.resolve(component.getName(), blockValues.keySet())
            .orElseThrow(() -> new IllegalArgumentException(
                "Field '" + component.getName() + "' not found in block '" + resolvedBlock + "'"));

        ViewState.Value value = blockValues.get(fieldKey);
        Object rawValue = ValueExtractor.extract(value);
        args[i] = ValueConverter.convert(rawValue, component.getType(), conversionService);
      }

      try {
        Class<?>[] paramTypes = new Class[components.length];
        for (int i = 0; i < components.length; i++) {
          paramTypes[i] = components[i].getType();
        }
        return recordType.getDeclaredConstructor(paramTypes).newInstance(args);
      } catch (Exception e) {
        throw new RuntimeException("Failed to construct " + recordType.getSimpleName(), e);
      }
    };
  }
}
```

**Step 4: Run test to verify it passes**

Run: `mvn clean test -pl bolt-spring-boot-autoconfigure -Dtest='BlockParameterResolverTest'`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: add BlockParameterResolver for @Block record binding"
```

---

### Task 9: ParameterResolverFactory

The factory that selects the right resolver for each method parameter based on its annotations and type.

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/resolver/ParameterResolverFactoryTest.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/resolver/ParameterResolverFactory.java`

**Step 1: Write the failing test**

Test key scenarios:
- Parameter with `@UserId` → returns UserIdResolver
- Parameter with type `SlashCommandRequest` → returns request resolver
- Parameter with type `SlashCommandContext` → returns context resolver
- Parameter with `@Block` → returns BlockParameterResolver
- Unresolvable parameter → throws at factory time (fail fast)

**Step 2-5: TDD cycle**

The factory inspects `java.lang.reflect.Parameter` annotations and type to select the resolver. It needs to know the handler's request/context types (passed in by the customizer).

**Commit:**

```bash
git add .
git commit -m "feat: add ParameterResolverFactory for parameter-to-resolver mapping"
```

---

### Task 10: Refactor AnnotationDrivenAppCustomizer

Replace the direct `invokeHandler(method, bean, req, ctx)` calls with the resolver chain.

**Files:**
- Modify: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/registrar/AnnotationDrivenAppCustomizer.java`
- Modify: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/registrar/AnnotationDrivenAppCustomizerTest.java`

**Changes:**
1. Inject `ConversionService` into the customizer (via constructor)
2. When registering a handler, build `ParameterResolver[]` via `ParameterResolverFactory`
3. Replace `(req, ctx) -> invokeHandler(method, target, req, ctx)` with `(req, ctx) -> invokeWithResolvers(method, target, resolvers, req, ctx)`
4. The `invokeWithResolvers` method iterates resolvers, builds args array, calls `method.invoke`

**Backward compatibility:** Existing tests with `(Request, Context)` signatures must still pass — the factory resolves those via type matching.

**New tests:** Add tests with mixed signatures (annotated + raw params, annotated only).

**Commit:**

```bash
git add .
git commit -m "feat: refactor handler registration to use parameter resolver chain"
```

---

### Task 11: Integration Tests

End-to-end tests verifying the parameter injection works through auto-configuration.

**Files:**
- Modify: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/SlackAppIntegrationTest.java`

**Tests:**
- Handler with `@CommandText` + `@UserId` (no raw request/context)
- Handler with `@Block` record parameter
- Handler mixing annotated params with raw context
- Original fixed-signature handler still works

**Commit:**

```bash
git add .
git commit -m "test: add integration tests for parameter injection"
```

---

### Task 12: Update Documentation

- Update README with parameter injection examples
- Update CHANGELOG for v0.3.0
- Add javadoc to all new public API classes

**Commit:**

```bash
git add .
git commit -m "docs: document parameter injection for v0.3.0"
```
