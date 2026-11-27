# Repository Guidelines

## Project Structure & Module Organization

This repository builds `no.novari:fint-core-principal`, a Kotlin library for converting FINT JWT claims into a typed Spring Security principal. It does not provide a standalone application or configure a service’s security filter chain.

- `src/main/kotlin/no/novari/resource/server/`: principal, converter, claim constants, enums, and issuer-default configuration.
- `src/main/resources/`: `application.yaml` and `META-INF/spring.factories` registration.
- `src/test/kotlin/`: tests mirroring the production package structure.
- `build.gradle.kts`: dependencies, Java toolchain, and Maven publishing; `gradle/wrapper/`: pinned Gradle distribution.
- `.github/workflows/`: CI builds and release-triggered publishing.

## Build, Test, and Development Commands

Use JDK 21 and the checked-in Gradle wrapper.

- `./gradlew clean build --no-daemon`: compile, run tests, and package the library; matches CI.
- `./gradlew test`: run the test suite.
- `./gradlew test --tests '*CorePrincipalTest'`: run principal tests only.
- `./gradlew publishToMavenLocal`: install artifacts locally for testing in a consuming service.

There is no standalone local server; executable Boot JAR generation is disabled. Test integration in a consuming application.

## Coding Style & Naming Conventions

Use four-space indentation in Kotlin source and preserve the existing tab indentation in Gradle scripts. Keep packages under `no.novari.resource.server`. Use PascalCase for types, camelCase for functions and properties, and uppercase snake case for claim constants. Prefer immutable values and explicit null handling for JWT claims. No formatter or lint plugin is configured; follow surrounding code.

## Testing Guidelines

Tests use JUnit Jupiter, AssertJ, and MockK through JUnit Platform. Name files `<Type>Test.kt`; use descriptive backtick method names and `@Nested` groups for related behavior. Cover claim parsing, missing or unknown values, and principal/converter behavior when changing those paths. No coverage threshold is configured. Run the full build before submitting.

## Commit & Pull Request Guidelines

Recent commits use prefixes such as `feat:`, `docs:`, and `chore:`. Write concise, imperative subjects. In PR descriptions, explain the behavior change, relevant issue links, compatibility implications, and validation commands/results. Update `README.md` when public APIs or usage change.

## Security & Configuration

Keep authorization rules and JWT decoder configuration in consuming services. Preserve the overridable issuer-URI default. Never commit tokens or publishing credentials; release publishing reads `REPOSILITE_USERNAME` and `REPOSILITE_PASSWORD` from the environment.
