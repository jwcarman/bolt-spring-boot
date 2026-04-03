# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/), and this project adheres to [Semantic Versioning](https://semver.org/).

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

[0.1.0]: https://github.com/jwcarman/bolt-spring-boot/releases/tag/0.1.0
