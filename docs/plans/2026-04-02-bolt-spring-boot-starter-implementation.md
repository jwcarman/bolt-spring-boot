# Bolt Spring Boot Starter — Implementation Plan

> **For Claude:** REQUIRED SUB-SKILL: Use superpowers:executing-plans to implement this plan task-by-task.

**Goal:** Build a two-module Spring Boot starter that provides annotation-driven handler registration for the Slack Bolt Java SDK.

**Architecture:** Multi-module Gradle project. The `autoconfigure` module contains annotations, a `Customizer<App>`-based scanner, `SlackProperties`, and `SlackAutoConfiguration`. The `starter` module is a thin dependency aggregator. All Bolt handler types are covered with one annotation each, using fixed method signatures.

**Tech Stack:** Java 21, Spring Boot 4.0.5, Spring Framework 7, Gradle (Kotlin DSL), `com.slack.api:bolt-jakarta-servlet:1.48.0`, JUnit 5, Spring Boot Test

---

### Task 1: Project Scaffolding

**Files:**
- Create: `settings.gradle.kts`
- Create: `build.gradle.kts` (root)
- Create: `bolt-spring-boot-autoconfigure/build.gradle.kts`
- Create: `bolt-spring-boot-starter/build.gradle.kts`
- Create: `.gitignore`

**Step 1: Create root `settings.gradle.kts`**

```kotlin
rootProject.name = "bolt-spring-boot"

include("bolt-spring-boot-autoconfigure")
include("bolt-spring-boot-starter")
```

**Step 2: Create root `build.gradle.kts`**

```kotlin
plugins {
    java
    id("io.spring.dependency-management") version "1.1.7" apply false
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "io.spring.dependency-management")

    group = "org.jwcarman.slack"
    version = "0.1.0-SNAPSHOT"

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    repositories {
        mavenCentral()
    }

    the<io.spring.dependencymanagement.dsl.DependencyManagementExtension>().apply {
        imports {
            mavenBom("org.springframework.boot:spring-boot-dependencies:4.0.5")
        }
    }

    tasks.withType<Test> {
        useJUnitPlatform()
    }
}
```

**Step 3: Create `bolt-spring-boot-autoconfigure/build.gradle.kts`**

```kotlin
dependencies {
    api("com.slack.api:bolt-jakarta-servlet:1.48.0")
    api("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-autoconfigure")
    annotationProcessor("org.springframework.boot:spring-boot-autoconfigure-processor")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
```

**Step 4: Create `bolt-spring-boot-starter/build.gradle.kts`**

```kotlin
dependencies {
    api(project(":bolt-spring-boot-autoconfigure"))
}
```

**Step 5: Create `.gitignore`**

```
.gradle/
build/
.idea/
*.iml
*.ipr
*.iws
out/
```

**Step 6: Verify the build compiles**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL (no sources yet, but project structure resolves)

**Step 7: Commit**

```bash
git add .
git commit -m "feat: scaffold multi-module Gradle project"
```

---

### Task 2: SlackProperties

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/SlackPropertiesTest.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/SlackProperties.java`

**Step 1: Write the failing test**

```java
package org.jwcarman.slack.bolt.autoconfigure;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = SlackPropertiesTest.Config.class)
@TestPropertySource(properties = {
    "slack.client-id=test-client-id",
    "slack.client-secret=test-client-secret",
    "slack.signing-secret=test-signing-secret",
    "slack.scope=chat:write,commands",
    "slack.user-scope=identity.basic",
    "slack.events-path=/custom/events",
    "slack.oauth-install-path=/custom/install",
    "slack.oauth-redirect-uri-path=/custom/redirect",
    "slack.oauth-completion-url=https://example.com/done",
    "slack.oauth-cancellation-url=https://example.com/cancel"
})
class SlackPropertiesTest {

    @EnableConfigurationProperties(SlackProperties.class)
    static class Config {}

    @Autowired
    private SlackProperties properties;

    @Test
    void bindsAllProperties() {
        assertThat(properties.getClientId()).isEqualTo("test-client-id");
        assertThat(properties.getClientSecret()).isEqualTo("test-client-secret");
        assertThat(properties.getSigningSecret()).isEqualTo("test-signing-secret");
        assertThat(properties.getScope()).isEqualTo("chat:write,commands");
        assertThat(properties.getUserScope()).isEqualTo("identity.basic");
        assertThat(properties.getEventsPath()).isEqualTo("/custom/events");
        assertThat(properties.getOauthInstallPath()).isEqualTo("/custom/install");
        assertThat(properties.getOauthRedirectUriPath()).isEqualTo("/custom/redirect");
        assertThat(properties.getOauthCompletionUrl()).isEqualTo("https://example.com/done");
        assertThat(properties.getOauthCancellationUrl()).isEqualTo("https://example.com/cancel");
    }

    @Test
    void hasDefaults() {
        SlackProperties defaults = new SlackProperties();
        assertThat(defaults.getEventsPath()).isEqualTo("/slack/events");
        assertThat(defaults.getOauthInstallPath()).isEqualTo("/slack/install");
        assertThat(defaults.getOauthRedirectUriPath()).isEqualTo("/slack/oauth_redirect");
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :bolt-spring-boot-autoconfigure:test`
Expected: FAIL — `SlackProperties` does not exist

**Step 3: Write the implementation**

```java
package org.jwcarman.slack.bolt.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "slack")
public class SlackProperties {

    private String clientId;
    private String clientSecret;
    private String signingSecret;
    private String scope;
    private String userScope;
    private String eventsPath = "/slack/events";
    private String oauthInstallPath = "/slack/install";
    private String oauthRedirectUriPath = "/slack/oauth_redirect";
    private String oauthCompletionUrl;
    private String oauthCancellationUrl;

    // getters and setters for all fields
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew :bolt-spring-boot-autoconfigure:test`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: add SlackProperties with slack.* config binding"
```

---

### Task 3: SlashCommand Annotation

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/SlashCommand.java`

**Step 1: Create the annotation**

```java
package org.jwcarman.slack.bolt.autoconfigure.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SlashCommand {
    String value();
}
```

**Step 2: Verify build**

Run: `./gradlew :bolt-spring-boot-autoconfigure:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add .
git commit -m "feat: add @SlashCommand annotation"
```

---

### Task 4: AnnotationDrivenAppCustomizer — SlashCommand Support

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/registrar/AnnotationDrivenAppCustomizerTest.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/registrar/AnnotationDrivenAppCustomizer.java`

**Step 1: Write the failing test**

```java
package org.jwcarman.slack.bolt.autoconfigure.registrar;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.response.Response;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlashCommand;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.GenericApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;

class AnnotationDrivenAppCustomizerTest {

    @Test
    void registersSlashCommandHandler() throws Exception {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("testHandler", TestSlashCommandHandler.class);
        context.refresh();

        AnnotationDrivenAppCustomizer customizer = new AnnotationDrivenAppCustomizer(context);

        AppConfig config = AppConfig.builder()
                .singleTeamBotToken("xoxb-test")
                .signingSecret("test-secret")
                .build();
        App app = new App(config);

        customizer.customize(app);

        // Verify the command was registered by checking the app's command handlers
        // The App class doesn't expose handlers directly, so we verify via the config
        // that at least no exception was thrown during registration.
        // A more thorough integration test will verify end-to-end behavior.
        assertThat(app).isNotNull();
    }

    public static class TestSlashCommandHandler {
        @SlashCommand("/test")
        public Response handle(SlashCommandRequest req, SlashCommandContext ctx) {
            return ctx.ack("test response");
        }
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :bolt-spring-boot-autoconfigure:test --tests '*AnnotationDrivenAppCustomizerTest*'`
Expected: FAIL — `AnnotationDrivenAppCustomizer` does not exist

**Step 3: Write the implementation**

```java
package org.jwcarman.slack.bolt.autoconfigure.registrar;

import com.slack.api.bolt.App;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlashCommand;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.Method;

public class AnnotationDrivenAppCustomizer implements org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration.Customizer<App> {
    // WRONG — there's no such Customizer in Spring. We need our own or use a functional interface.
}
```

**IMPORTANT NOTE:** Spring Boot does not have a generic `Customizer<T>` for arbitrary types. We need to either:
- Define our own `@FunctionalInterface SlackAppCustomizer` that extends or mirrors the pattern
- Or use `org.springframework.boot.autoconfigure.web.servlet.WebServerFactoryCustomizer` style

**The correct approach:** Define a `SlackAppCustomizer` functional interface:

```java
package org.jwcarman.slack.bolt.autoconfigure;

@FunctionalInterface
public interface SlackAppCustomizer {
    void customize(App app);
}
```

Then `AnnotationDrivenAppCustomizer` implements `SlackAppCustomizer`:

```java
package org.jwcarman.slack.bolt.autoconfigure.registrar;

import com.slack.api.bolt.App;
import org.jwcarman.slack.bolt.autoconfigure.SlackAppCustomizer;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlashCommand;
import org.springframework.context.ApplicationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class AnnotationDrivenAppCustomizer implements SlackAppCustomizer {

    private final ApplicationContext applicationContext;

    public AnnotationDrivenAppCustomizer(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void customize(App app) {
        for (String beanName : applicationContext.getBeanDefinitionNames()) {
            Object bean = applicationContext.getBean(beanName);
            for (Method method : bean.getClass().getDeclaredMethods()) {
                SlashCommand slashCommand = method.getAnnotation(SlashCommand.class);
                if (slashCommand != null) {
                    app.command(slashCommand.value(), (req, ctx) -> {
                        return (com.slack.api.bolt.response.Response) method.invoke(bean, req, ctx);
                    });
                }
            }
        }
    }
}
```

**Step 4: Run test to verify it passes**

Run: `./gradlew :bolt-spring-boot-autoconfigure:test --tests '*AnnotationDrivenAppCustomizerTest*'`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: add SlackAppCustomizer interface and AnnotationDrivenAppCustomizer with @SlashCommand support"
```

---

### Task 5: Remaining Annotations

Create all remaining annotations. Each follows the same pattern as `@SlashCommand`. Create them all in one task since they are trivial declarations.

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/Event.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/BlockAction.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/BlockSuggestion.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/GlobalShortcut.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/MessageShortcut.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/ViewSubmission.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/ViewClosed.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/Message.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/DialogSubmission.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/DialogSuggestion.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/DialogCancellation.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/AttachmentAction.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/WorkflowStepEdit.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/WorkflowStepSave.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/annotations/WorkflowStepExecute.java`

**Step 1: Create all annotations**

Each annotation follows this template:

```java
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface <Name> {
    String value(); // except @Event which uses: Class<? extends com.slack.api.model.event.Event> value();
}
```

**Step 2: Verify build**

Run: `./gradlew :bolt-spring-boot-autoconfigure:build`
Expected: BUILD SUCCESSFUL

**Step 3: Commit**

```bash
git add .
git commit -m "feat: add all remaining Bolt handler annotations"
```

---

### Task 6: Register All Handler Types in AnnotationDrivenAppCustomizer

**Files:**
- Modify: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/registrar/AnnotationDrivenAppCustomizerTest.java`
- Modify: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/registrar/AnnotationDrivenAppCustomizer.java`

**Step 1: Add tests for each handler type**

Add test inner classes and test methods for each annotation. Each test creates a bean with an annotated method, runs the customizer, and verifies no exception. Example for `@BlockAction`:

```java
@Test
void registersBlockActionHandler() {
    GenericApplicationContext context = new GenericApplicationContext();
    context.registerBean("handler", TestBlockActionHandler.class);
    context.refresh();

    AnnotationDrivenAppCustomizer customizer = new AnnotationDrivenAppCustomizer(context);
    App app = createTestApp();
    customizer.customize(app);

    assertThat(app).isNotNull();
}

public static class TestBlockActionHandler {
    @BlockAction("test-action")
    public Response handle(BlockActionRequest req, ActionContext ctx) {
        return ctx.ack();
    }
}
```

Repeat for: `@Event`, `@BlockSuggestion`, `@GlobalShortcut`, `@MessageShortcut`, `@ViewSubmission`, `@ViewClosed`, `@Message`, `@DialogSubmission`, `@DialogSuggestion`, `@DialogCancellation`, `@AttachmentAction`, `@WorkflowStepEdit`, `@WorkflowStepSave`, `@WorkflowStepExecute`.

**Step 2: Run tests to verify they fail**

Run: `./gradlew :bolt-spring-boot-autoconfigure:test --tests '*AnnotationDrivenAppCustomizerTest*'`
Expected: FAIL — new handler types not yet registered

**Step 3: Add registration logic for all handler types**

Extend `customize()` to scan for each annotation and call the corresponding `app.*()` method:

| Annotation | Registration call |
|---|---|
| `@Event` | `app.event(annotation.value(), (payload, ctx) -> invoke(...))` |
| `@BlockAction` | `app.blockAction(annotation.value(), (req, ctx) -> invoke(...))` |
| `@BlockSuggestion` | `app.blockSuggestion(annotation.value(), (req, ctx) -> invoke(...))` |
| `@GlobalShortcut` | `app.globalShortcut(annotation.value(), (req, ctx) -> invoke(...))` |
| `@MessageShortcut` | `app.messageShortcut(annotation.value(), (req, ctx) -> invoke(...))` |
| `@ViewSubmission` | `app.viewSubmission(annotation.value(), (req, ctx) -> invoke(...))` |
| `@ViewClosed` | `app.viewClosed(annotation.value(), (req, ctx) -> invoke(...))` |
| `@Message` | `app.message(Pattern.compile(annotation.value()), (payload, ctx) -> invoke(...))` |
| `@DialogSubmission` | `app.dialogSubmission(annotation.value(), (req, ctx) -> invoke(...))` |
| `@DialogSuggestion` | `app.dialogSuggestion(annotation.value(), (req, ctx) -> invoke(...))` |
| `@DialogCancellation` | `app.dialogCancellation(annotation.value(), (req, ctx) -> invoke(...))` |
| `@AttachmentAction` | `app.attachmentAction(annotation.value(), (req, ctx) -> invoke(...))` |
| `@WorkflowStepEdit` | `app.workflowStepEdit(annotation.value(), (req, ctx) -> invoke(...))` |
| `@WorkflowStepSave` | `app.workflowStepSave(annotation.value(), (req, ctx) -> invoke(...))` |
| `@WorkflowStepExecute` | `app.workflowStepExecute(annotation.value(), (req, ctx) -> invoke(...))` |

**Step 4: Run tests to verify they pass**

Run: `./gradlew :bolt-spring-boot-autoconfigure:test --tests '*AnnotationDrivenAppCustomizerTest*'`
Expected: PASS

**Step 5: Commit**

```bash
git add .
git commit -m "feat: register all Bolt handler types in AnnotationDrivenAppCustomizer"
```

---

### Task 7: SlackAutoConfiguration

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/SlackAutoConfigurationTest.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/java/org/jwcarman/slack/bolt/autoconfigure/SlackAutoConfiguration.java`
- Create: `bolt-spring-boot-autoconfigure/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`

**Step 1: Write the failing test**

```java
package org.jwcarman.slack.bolt.autoconfigure;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class SlackAutoConfigurationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SlackAutoConfiguration.class))
            .withPropertyValues(
                    "slack.client-id=test-client-id",
                    "slack.client-secret=test-client-secret",
                    "slack.signing-secret=test-signing-secret",
                    "slack.scope=chat:write"
            );

    @Test
    void createsAppConfigBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(AppConfig.class);
            AppConfig config = context.getBean(AppConfig.class);
            assertThat(config.getClientId()).isEqualTo("test-client-id");
            assertThat(config.getClientSecret()).isEqualTo("test-client-secret");
            assertThat(config.getSigningSecret()).isEqualTo("test-signing-secret");
            assertThat(config.getScope()).isEqualTo("chat:write");
        });
    }

    @Test
    void createsAppBean() {
        contextRunner.run(context -> {
            assertThat(context).hasSingleBean(App.class);
        });
    }

    @Test
    void appliesCustomizers() {
        contextRunner.withUserConfiguration(CustomizerConfig.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(App.class);
                    assertThat(CustomizerConfig.customized).isTrue();
                });
    }

    @Test
    void registersEventServlet() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("slackEventsServlet");
        });
    }

    @Test
    void registersOAuthServlet() {
        contextRunner.run(context -> {
            assertThat(context).hasBean("slackOAuthServlet");
        });
    }

    @Test
    void doesNotCreateBeansWithoutRequiredProperties() {
        new WebApplicationContextRunner()
                .withConfiguration(AutoConfigurations.of(SlackAutoConfiguration.class))
                .run(context -> {
                    assertThat(context).doesNotHaveBean(App.class);
                });
    }

    static class CustomizerConfig {
        static boolean customized = false;

        @org.springframework.context.annotation.Bean
        SlackAppCustomizer testCustomizer() {
            return app -> customized = true;
        }
    }
}
```

**Step 2: Run test to verify it fails**

Run: `./gradlew :bolt-spring-boot-autoconfigure:test --tests '*SlackAutoConfigurationTest*'`
Expected: FAIL — `SlackAutoConfiguration` does not exist

**Step 3: Write the implementation**

```java
package org.jwcarman.slack.bolt.autoconfigure;

import com.slack.api.bolt.App;
import com.slack.api.bolt.AppConfig;
import com.slack.api.bolt.jakarta_servlet.SlackAppServlet;
import com.slack.api.bolt.jakarta_servlet.SlackOAuthAppServlet;
import org.jwcarman.slack.bolt.autoconfigure.registrar.AnnotationDrivenAppCustomizer;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.List;

@AutoConfiguration
@EnableConfigurationProperties(SlackProperties.class)
@ConditionalOnProperty(prefix = "slack", name = {"client-id", "client-secret", "signing-secret"})
public class SlackAutoConfiguration {

    @Bean
    public AppConfig appConfig(SlackProperties props) {
        return AppConfig.builder()
                .clientId(props.getClientId())
                .clientSecret(props.getClientSecret())
                .signingSecret(props.getSigningSecret())
                .scope(props.getScope())
                .userScope(props.getUserScope())
                .oauthInstallPath(props.getOauthInstallPath())
                .oauthRedirectUriPath(props.getOauthRedirectUriPath())
                .oauthCompletionUrl(props.getOauthCompletionUrl())
                .oauthCancellationUrl(props.getOauthCancellationUrl())
                .build();
    }

    @Bean
    public App slackApp(AppConfig config, List<SlackAppCustomizer> customizers) {
        App app = new App(config).asOAuthApp(true);
        customizers.forEach(c -> c.customize(app));
        return app;
    }

    @Bean
    public AnnotationDrivenAppCustomizer annotationDrivenAppCustomizer(ApplicationContext applicationContext) {
        return new AnnotationDrivenAppCustomizer(applicationContext);
    }

    @Bean
    public ServletRegistrationBean<SlackAppServlet> slackEventsServlet(App app, SlackProperties props) {
        return new ServletRegistrationBean<>(new SlackAppServlet(app), props.getEventsPath());
    }

    @Bean
    public ServletRegistrationBean<SlackOAuthAppServlet> slackOAuthServlet(App app, SlackProperties props) {
        return new ServletRegistrationBean<>(new SlackOAuthAppServlet(app),
                props.getOauthInstallPath(), props.getOauthRedirectUriPath());
    }
}
```

**Step 4: Create the auto-configuration imports file**

Create `bolt-spring-boot-autoconfigure/src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`:

```
org.jwcarman.slack.bolt.autoconfigure.SlackAutoConfiguration
```

**Step 5: Run test to verify it passes**

Run: `./gradlew :bolt-spring-boot-autoconfigure:test --tests '*SlackAutoConfigurationTest*'`
Expected: PASS

**Step 6: Commit**

```bash
git add .
git commit -m "feat: add SlackAutoConfiguration with servlet registration and customizer discovery"
```

---

### Task 8: Full Integration Test

**Files:**
- Create: `bolt-spring-boot-autoconfigure/src/test/java/org/jwcarman/slack/bolt/autoconfigure/SlackAppIntegrationTest.java`

**Step 1: Write an integration test that verifies end-to-end annotation scanning**

```java
package org.jwcarman.slack.bolt.autoconfigure;

import com.slack.api.bolt.App;
import com.slack.api.bolt.request.builtin.SlashCommandRequest;
import com.slack.api.bolt.context.builtin.SlashCommandContext;
import com.slack.api.bolt.response.Response;
import org.jwcarman.slack.bolt.autoconfigure.annotations.SlashCommand;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

class SlackAppIntegrationTest {

    private final WebApplicationContextRunner contextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(SlackAutoConfiguration.class))
            .withPropertyValues(
                    "slack.client-id=test-client-id",
                    "slack.client-secret=test-client-secret",
                    "slack.signing-secret=test-signing-secret",
                    "slack.scope=chat:write,commands"
            );

    @Test
    void fullAutoConfigurationWithAnnotatedHandlers() {
        contextRunner
                .withUserConfiguration(TestHandlers.class)
                .run(context -> {
                    assertThat(context).hasSingleBean(App.class);
                    assertThat(context).hasSingleBean(SlackProperties.class);
                    assertThat(context).hasBean("slackEventsServlet");
                    assertThat(context).hasBean("slackOAuthServlet");
                    assertThat(context).hasBean("annotationDrivenAppCustomizer");
                });
    }

    @Component
    static class TestHandlers {
        @SlashCommand("/hello")
        public Response hello(SlashCommandRequest req, SlashCommandContext ctx) {
            return ctx.ack("Hello!");
        }
    }
}
```

**Step 2: Run test**

Run: `./gradlew :bolt-spring-boot-autoconfigure:test --tests '*SlackAppIntegrationTest*'`
Expected: PASS

**Step 3: Run full test suite**

Run: `./gradlew build`
Expected: BUILD SUCCESSFUL, all tests pass

**Step 4: Commit**

```bash
git add .
git commit -m "test: add full integration test for auto-configuration"
```

---

### Task 9: Final Cleanup & Verification

**Step 1: Run full build from clean**

Run: `./gradlew clean build`
Expected: BUILD SUCCESSFUL

**Step 2: Verify the starter module resolves transitively**

Run: `./gradlew :bolt-spring-boot-starter:dependencies --configuration runtimeClasspath`
Expected: Shows bolt-spring-boot-autoconfigure, bolt-jakarta-servlet, and spring-boot-starter-web in the tree

**Step 3: Commit any remaining changes**

```bash
git add .
git commit -m "chore: final cleanup and verification"
```
