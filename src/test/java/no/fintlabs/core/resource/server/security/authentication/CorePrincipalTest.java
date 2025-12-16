package no.fintlabs.core.resource.server.security.authentication;

import no.fintlabs.core.resource.server.security.JwtClaimsConstants;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CorePrincipalTest {

    @Mock
    private Jwt jwt;

    private final List<GrantedAuthority> authorities = Collections.emptyList();

    @Test
    @DisplayName("Constructor should map all fields correctly when all claims are present")
    void constructor_HappyPath() {
        // Arrange
        when(jwt.getClaimAsString(JwtClaimsConstants.FINT_ASSET_IDS)).thenReturn("asset1,asset2");
        when(jwt.getClaimAsString(JwtClaimsConstants.USERNAME)).thenReturn("johndoe");
        when(jwt.getClaimAsStringList(JwtClaimsConstants.SCOPE)).thenReturn(List.of("fint:read", "fint:write"));
        when(jwt.getClaimAsStringList(JwtClaimsConstants.ROLES)).thenReturn(List.of("ROLE_ADMIN"));

        // Act
        CorePrincipal principal = new CorePrincipal(jwt, authorities);

        // Assert
        assertEquals("johndoe", principal.getUsername());

        // Assets
        assertTrue(principal.containsAsset("asset1"));
        assertTrue(principal.containsAsset("asset2"));
        assertEquals(2, principal.getAssets().size());

        // Scopes
        assertTrue(principal.hasScope("fint:read"));
        assertFalse(principal.hasScope("random:scope"));

        // Roles
        assertTrue(principal.hasRole("ROLE_ADMIN"));
    }

}
