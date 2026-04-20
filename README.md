# fint-core-principal

A small Spring Boot library that turns a FINT Core JWT into a strongly-typed Kotlin principal.

## What it gives you

- **`CorePrincipal`** — a `JwtAuthenticationToken` subclass that exposes the fields a FINT service actually cares about:
  - `username` (`cn` claim)
  - `assets` (parsed from `fintAssetIDs`)
  - `orgId` (first asset with `-` replaced by `.`)
  - `type: FintType` (`CLIENT` or `ADAPTER`, parsed from the username)
  - `scopes: Set<FintScope>` (`FINT_CLIENT` / `FINT_ADAPTER`)
- **`CorePrincipalConverter`** — a `Converter<Jwt, AbstractAuthenticationToken>` you plug into your own `SecurityWebFilterChain` / `SecurityFilterChain`.
- An overridable default for `spring.security.oauth2.resourceserver.jwt.issuer-uri` so you don't have to configure the FINT IdP URL in every service.

## What it does NOT give you

This library deliberately does **not** configure security. No `SecurityWebFilterChain`, no `JwtDecoder`, no authorization rules, no OPA integration. Each service owns its own security configuration and wires `CorePrincipalConverter` in itself.

## Usage

Add the dependency:

```kotlin
dependencies {
    implementation("no.novari:fint-core-principal:<version>")
}
```

Wire the converter into your security config:

```kotlin
@Bean
fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
    http
        .oauth2ResourceServer { it.jwt { jwt ->
            jwt.jwtAuthenticationConverter(
                ReactiveJwtAuthenticationConverterAdapter(CorePrincipalConverter())
            )
        } }
        .authorizeExchange { it.anyExchange().authenticated() }
        .build()
```

Then consume the principal in your handlers:

```kotlin
fun handle(principal: CorePrincipal) {
    if (principal.type == FintType.CLIENT && FintScope.FINT_CLIENT in principal.scopes) {
        // ...
    }
}
```

Override the default issuer URI if needed:

```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: https://your-idp.example.com/...
```

## Compatibility

- Spring Boot 3.x and 4.x (one artifact for both — verified against the stable `oauth2-resource-server` and deprecated-but-present `EnvironmentPostProcessor` APIs)
- JDK 21+
- Kotlin 2.x
