# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this project adheres to [Semantic Versioning](https://semver.org/).

## [0.3.0] - 2026-04-04

### Added

- Flexible parameter injection for handler methods using binding annotations
- Universal binding annotations: `@UserId`, `@UserName`, `@TeamId`, `@ChannelId`, `@TriggerId`, `@ResponseUrl`
- Handler-specific binding annotations: `@CommandText`, `@ActionValue`, `@MessageText`
- `@Block` annotation for binding view submission state to Java records
- `@ActionId` annotation for overriding field-to-action-ID matching in `@Block` records
- Convention-based name matching (exact, kebab-case, snake_case, case-insensitive)
- Type coercion via Spring's `ConversionService`
- Flexible return types: `Response`, `String` (auto-ack with text), `void` (auto-ack), or any object (JSON serialization)
- Mixed parameter support: annotated params and raw request/context in the same method
- `ParameterBindingFactory` and `MethodBindingFactory` as Spring beans
- All Bolt input element types supported in `@Block` binding (text, select, multi-select, datepicker, timepicker, file, rich text, etc.)

### Changed

- `AnnotationDrivenAppCustomizer` refactored to use `MethodBindingFactory` for handler registration
- Removed Mockito from tests — all tests use real Bolt SDK objects for full IDEA compatibility

## [0.2.2] - 2026-04-03

### Fixed

- Changed starter module from `pom` to `jar` packaging so consumers don't need `<type>pom</type>` in their dependency

## [0.2.1] - 2026-04-03

### Added

- Spring configuration metadata (`spring-configuration-metadata.json`) for IDE autocompletion and property documentation

### Changed

- Moved annotation processors to compiler plugin `annotationProcessorPaths` (removes unnecessary optional dependencies)

## [0.2.0] - 2026-04-03

### Added

- Single-team mode support via `slack.bot-token` property
- Auto-detection of mode based on which properties are present
- Startup logging indicating which mode is active

### Changed

- `SlackAutoConfiguration` refactored to use `@Conditional` inner classes for single-team vs OAuth mode
- `scope` and `user-scope` properties only applied in OAuth mode

## [0.1.0] - 2026-04-03

### Added

- `@SlackController` stereotype annotation for handler beans
- 13 handler annotations: `@SlashCommand`, `@Event`, `@BlockAction`, `@BlockSuggestion`, `@GlobalShortcut`, `@MessageShortcut`, `@ViewSubmission`, `@ViewClosed`, `@Message`, `@DialogSubmission`, `@DialogSuggestion`, `@DialogCancellation`, `@AttachmentAction`
- `SlackAutoConfiguration` with OAuth servlet registration and customizer discovery
- `SlackAppCustomizer` functional interface for programmatic `App` customization
- `SlackProperties` configuration bound to `slack.*` prefix
- `SlackHandlerInvocationException` for consistent error handling
- `AnnotationDrivenAppCustomizer` using `MethodIntrospector` for proxy-safe scanning
- Maven Central publishing via GitHub Actions
- CI with SonarCloud analysis and JaCoCo coverage

[0.3.0]: https://github.com/jwcarman/bolt-spring-boot/releases/tag/0.3.0
[0.2.2]: https://github.com/jwcarman/bolt-spring-boot/releases/tag/0.2.2
[0.2.1]: https://github.com/jwcarman/bolt-spring-boot/releases/tag/0.2.1
[0.2.0]: https://github.com/jwcarman/bolt-spring-boot/releases/tag/0.2.0
[0.1.0]: https://github.com/jwcarman/bolt-spring-boot/releases/tag/0.1.0
