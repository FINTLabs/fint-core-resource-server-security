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

    @Override
    public String toString() {
        return "CorePrincipal{" +
                "orgId='" + orgId + '\'' +
                ", username='" + username + '\'' +
                ", scopes=" + scopes +
                ", roles=" + roles +
                ", " + super.toString() +
                '}';
    }

}
