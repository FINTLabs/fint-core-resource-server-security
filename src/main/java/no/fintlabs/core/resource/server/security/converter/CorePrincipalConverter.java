package no.fintlabs.core.resource.server.security.converter;

import no.fintlabs.core.resource.server.security.authentication.CorePrincipal;
import no.vigoiks.resourceserver.security.FintJwtDefaultConverter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

import static no.fintlabs.core.resource.server.security.JwtClaimsConstants.FINT_ASSET_IDS;
import static no.fintlabs.core.resource.server.security.JwtClaimsConstants.ROLES;

public class CorePrincipalConverter extends FintJwtDefaultConverter {

    public CorePrincipalConverter() {
        this.addMapping(FINT_ASSET_IDS, "ORGID_");
        this.addMapping(ROLES, "ROLE_");
    }

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        return super.convert(jwt).map(token -> new CorePrincipal(jwt, token.getAuthorities()));
    }

}
