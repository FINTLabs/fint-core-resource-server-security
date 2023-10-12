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
        HashSet<String> roles = new HashSet<>(jwt.getClaimAsStringList(ROLES));
        String orgId = jwt.getClaimAsString(FINT_ASSET_IDS);
        String scope = jwt.getClaimAsString(SCOPE);
        String username = jwt.getClaimAsString(USERNAME);

        return super.convert(jwt)
                .flatMap(abstractAuthenticationToken ->
                        Mono.just(new CorePrincipal(
                                jwt,
                                abstractAuthenticationToken.getAuthorities(),
                                orgId,
                                scope,
                                username,
                                roles)
                        )
                );
    }

}
