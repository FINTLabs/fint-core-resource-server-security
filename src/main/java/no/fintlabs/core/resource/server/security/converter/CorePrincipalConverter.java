package no.fintlabs.core.resource.server.security.converter;

import no.fintlabs.core.resource.server.security.authentication.CorePrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.List;

public class CorePrincipalConverter extends CoreDefaultConverter {

    @Override
    protected AbstractAuthenticationToken createAuthenticationToken(Jwt jwt, List<GrantedAuthority> authorities) {
        return new CorePrincipal(jwt, authorities);
    }

}
