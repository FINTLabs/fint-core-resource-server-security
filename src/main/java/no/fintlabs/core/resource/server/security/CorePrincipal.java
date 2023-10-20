package no.fintlabs.core.resource.server.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.HashSet;

@Getter
public class CorePrincipal extends JwtAuthenticationToken {

    private final String orgId;
    private final String username;
    private final HashSet<String> scopes;
    private final HashSet<String> roles;

    public CorePrincipal(Jwt jwt,
                         Collection<? extends GrantedAuthority> authorities,
                         String orgId, String username,
                         HashSet<String> scopes,
                         HashSet<String> roles) {
        super(jwt, authorities);
        this.orgId = orgId;
        this.scopes = scopes;
        this.username = username;
        this.roles = roles;
    }

    public boolean hasScope(String scope) {
        return scopes.contains(scope);
    }

    public boolean hasRole(String role) {
        return roles.contains(role);
    }

    public boolean orgIdsMatch(String orgId) {
        return this.orgId.equals(orgId);
    }

    public boolean orgIdDoesntMatch(String orgId) {
        return !this.orgId.equals(orgId);
    }

}
