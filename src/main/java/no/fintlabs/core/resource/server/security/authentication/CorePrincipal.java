package no.fintlabs.core.resource.server.security.authentication;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
        this.assets = new HashSet<>(Arrays.asList(jwt.getClaimAsString(FINT_ASSET_IDS).split(",")));
        this.username = jwt.getClaimAsString(USERNAME);
        this.scopes = new HashSet<>(jwt.getClaimAsStringList(SCOPE));
        this.roles = new HashSet<>(jwt.getClaimAsStringList(ROLES));
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
