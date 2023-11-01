package no.fintlabs.core.resource.server.security.authentication;

import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.HashSet;

import static no.fintlabs.core.resource.server.security.JwtClaimsConstants.*;

@Getter
@ToString
public class CorePrincipal extends JwtAuthenticationToken {

    private final String orgId;
    private final String username;
    private final HashSet<String> scopes;
    private final HashSet<String> roles;

    public CorePrincipal(Jwt jwt, Collection<? extends GrantedAuthority> authorities) {
        super(jwt, authorities);
        this.orgId = jwt.getClaimAsString(FINT_ASSET_IDS);
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

    public boolean hasMatchingOrgId(String orgId) {
        return this.orgId.equals(orgId);
    }

    public boolean doesNotHaveMatchingOrgId(String orgId) {
        return !this.orgId.equals(orgId);
    }

}
