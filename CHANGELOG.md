# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this project adheres to [Semantic Versioning](https://semver.org/).

## [0.3.0] - 2026-04-03

### Added

- Parameter injection for handler methods using binding annotations
- Universal binding annotations: `@UserId`, `@UserName`, `@TeamId`, `@ChannelId`, `@TriggerId`, `@ResponseUrl`
- Handler-specific binding annotations: `@CommandText` (slash commands), `@ActionValue` (block actions), `@MessageText` (message handlers)
- `@Block` annotation for binding view submission state to records or POJOs
- `ParameterResolverFactory` for annotation-driven parameter resolution
- Convention-based name matching for `@Block` (camelCase, kebab-case, snake_case, UPPER_CASE)
- Type coercion via Spring's `ConversionService` for all resolved parameters
- Mixed parameter support: annotated params and raw request/context in the same method

### Changed

- `AnnotationDrivenAppCustomizer` refactored to use `ParameterResolver` chain instead of direct method invocation

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
