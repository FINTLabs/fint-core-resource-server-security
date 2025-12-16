package no.fintlabs.core.resource.server.security.authentication;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.*;

import static no.fintlabs.core.resource.server.security.JwtClaimsConstants.*;

@Getter
@ToString
public class CorePrincipal extends JwtAuthenticationToken {

    private final Set<String> assets;
    private final String username;
    private final HashSet<String> scopes;
    private final HashSet<String> roles;

    public CorePrincipal(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
        this.username = jwt.getClaimAsString(USERNAME);
        this.assets = extractAssets(jwt);
        this.scopes = extractClaimAsSet(jwt, SCOPE);
        this.roles = extractClaimAsSet(jwt, ROLES);
    }

    private Set<String> extractAssets(Jwt jwt) {
        String assetClaim = jwt.getClaimAsString(FINT_ASSET_IDS);

        if (assetClaim == null || assetClaim.isBlank()) {
            return new HashSet<>();
        }

        return new HashSet<>(Arrays.asList(assetClaim.split(",")));
    }

    private HashSet<String> extractClaimAsSet(Jwt jwt, String claimName) {
        List<String> claimList = jwt.getClaimAsStringList(claimName);

        if (claimList == null) {
            return new HashSet<>();
        }

        return new HashSet<>(claimList);
    }

    public boolean hasMatchingUsername(String username) {
        return this.username.equals(username);
    }

    public boolean doesNotHaveMatchingUsername(String username) {
        return !this.username.equals(username);
    }

    public boolean hasScope(String scope) {
        return scopes.contains(scope);
    }

    public boolean doesNotHaveScope(String scope) {
        return !scopes.contains(scope);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean doesNotHaveRole(String role) {
        return !roles.contains(role);
    }

    @Deprecated
    public boolean hasMatchingOrgId(String orgId) {
        return assets.contains(orgId);
    }

    public boolean containsAsset(String asset) {
        return assets.contains(asset);
    }

    public boolean doesNotContainAsset(String asset) {
        return !assets.contains(asset);
    }

    @Deprecated
    public boolean doesNotHaveMatchingOrgId(String orgId) {
        return !this.assets.contains(orgId);
    }

}
