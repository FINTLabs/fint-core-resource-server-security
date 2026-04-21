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

## Where did `SecurityConfiguration` / OPA / `CoreAccessService` go?

Earlier versions of this library (published as `no.fintlabs:core-resource-server`) shipped an opinionated `SecurityConfiguration` that:

- built a `SecurityWebFilterChain` for you,
- required every request to be authenticated,
- called out to Open Policy Agent (OPA) via `CoreAccessService` / `OpaService` / `OpaClient` to authorize requests,
- and pulled in `spring-boot-starter-webflux`, `reactor-netty`, `jackson-module-kotlin`, and friends as transitive dependencies.

All of that was removed in the 4.x rewrite. The library is now just `CorePrincipal` + `CorePrincipalConverter` + an overridable issuer-URI default.

**Why it was removed:**

- **Security config is service-specific.** The one-size-fits-all filter chain forced every service to opt out piecemeal whenever it needed a different rule (public endpoints, actuator exposure, custom matchers). Owning the `SecurityFilterChain` in each service is clearer than fighting an opinionated default.
- **OPA is a policy choice, not a library concern.** Not every service authorizes via OPA, and the ones that do want to control the client, caching, request shape, and failure behavior themselves. Bundling an OPA client here coupled unrelated services to the same OPA version, URL convention, and envelope format.
- **Transitive weight.** Shipping WebFlux + Reactor + Jackson-Kotlin as `api` dependencies leaked those choices into every consumer, including services on Spring MVC. The current library has a single `api` dependency: `spring-boot-starter-oauth2-resource-server`.
- **Spring Boot 3/4 compatibility.** The old auto-configured chain was tied to APIs that shifted between Boot 3 and Boot 4. Keeping the library to plain types (`Converter<Jwt, AbstractAuthenticationToken>`, `EnvironmentPostProcessor`) means one artifact works on both.

**What to do instead:**

- Build your own `SecurityFilterChain` (or `SecurityWebFilterChain`) in the service and wire in `CorePrincipalConverter` — see the usage example below.
- If you need OPA, call it from your own `AuthorizationManager` / interceptor / filter in the service. There is no replacement helper in this library.

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
