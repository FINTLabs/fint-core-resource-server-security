package no.fintlabs.resourceServer.security;

import no.vigoiks.resourceserver.security.FintJwtDefaultConverter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import reactor.core.publisher.Mono;

import java.util.HashSet;

public class CorePrincipalConverter extends FintJwtDefaultConverter {

    private final static String FINT_ASSET_IDS = "fintAssetIDs";
    private final static String ROLES = "Roles";
    private final static String SCOPE = "scope";
    private final static String USERNAME = "cn";

    public CorePrincipalConverter() {
        this.addMapping(FINT_ASSET_IDS, "ORGID_");
        this.addMapping(ROLES, "ROLE_");
    }

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        return super.convert(jwt).map(token -> new CorePrincipal(
                jwt,
                token.getAuthorities(),
                jwt.getClaimAsString(FINT_ASSET_IDS),
                jwt.getClaimAsString(USERNAME),
                new HashSet<>(jwt.getClaimAsStringList(SCOPE)),
                new HashSet<>(jwt.getClaimAsStringList(ROLES))
        ));
    }

}
