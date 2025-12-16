package no.fintlabs.core.resource.server.security.converter;

import no.fintlabs.core.resource.server.security.authentication.CorePrincipal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

import static no.fintlabs.core.resource.server.security.JwtClaimsConstants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class CorePrincipalConverterTest {

    private final CorePrincipalConverter converter = new CorePrincipalConverter();

    @Test
    @DisplayName("Robustness: Should NOT throw error when claims are missing, but return empty Principal")
    void convert_MissingClaims_ReturnsSafePrincipal() {
        // Arrange: A JWT that is completely missing 'fint.asset.ids', 'scope', and 'roles'
        Jwt brokenJwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "user123",
                        USERNAME, "johndoe"
                        // NOTICE: All other custom claims are missing
                )
        );

        // Act
        Mono<AbstractAuthenticationToken> resultMono = converter.convert(brokenJwt);

        // Assert
        StepVerifier.create(resultMono)
                .assertNext(auth -> {
                    // 1. Verify we got a valid object (NOT an exception)
                    assertThat(auth).isInstanceOf(CorePrincipal.class);
                    CorePrincipal p = (CorePrincipal) auth;

                    // 2. Verify the username survived
                    assertThat(p.getUsername()).isEqualTo("johndoe");

                    // 3. Verify the missing fields became Empty Sets (Safely)
                    assertThat(p.getAssets())
                            .isNotNull()
                            .isEmpty();

                    assertThat(p.getScopes())
                            .isNotNull()
                            .isEmpty();

                    assertThat(p.getRoles())
                            .isNotNull()
                            .isEmpty();

                    // 4. Verify helper methods return false instead of crashing
                    assertThat(p.hasRole("ADMIN")).isFalse();
                    assertThat(p.containsAsset("123")).isFalse();
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Happy Path: Converter should emit CorePrincipal when all claims exist")
    void convert_ValidClaims_EmitsPrincipal() {
        // Arrange: Create a valid JWT
        Jwt goodJwt = new Jwt(
                "token-value",
                Instant.now(),
                Instant.now().plusSeconds(60),
                Map.of("alg", "none"),
                Map.of(
                        "sub", "user123",
                        USERNAME, "johndoe",
                        FINT_ASSET_IDS, "org1,org2",
                        SCOPE, Collections.singletonList("fint:read"),
                        ROLES, Collections.singletonList("ROLE_USER")
                )
        );

        // Act
        Mono<AbstractAuthenticationToken> resultMono = converter.convert(goodJwt);

        // Assert
        StepVerifier.create(resultMono)
                .assertNext(auth -> {
                    assertThat(auth).isInstanceOf(CorePrincipal.class);
                    CorePrincipal principal = (CorePrincipal) auth;
                    assertThat(principal.getUsername()).isEqualTo("johndoe");
                    assertThat(principal.containsAsset("org1")).isTrue();
                })
                .verifyComplete();
    }

}
