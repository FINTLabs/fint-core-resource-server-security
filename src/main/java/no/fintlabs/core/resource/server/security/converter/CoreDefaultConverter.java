package no.fintlabs.core.resource.server.security.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static no.fintlabs.core.resource.server.security.JwtClaimsConstants.*;

public class CoreDefaultConverter implements Converter<Jwt, Mono<AbstractAuthenticationToken>> {

    protected HashMap<String, String> authoritiesMap = new HashMap<>() {
        {
            this.put(SCOPE, "SCOPE_");
            this.put(FINT_ASSET_IDS, "ORGID_");
            this.put(ROLES, "ROLE_");
        }
    };

    @Override
    public Mono<AbstractAuthenticationToken> convert(Jwt jwt) {
        return Flux.fromIterable(this.authoritiesMap.entrySet())
                .filter(claimPrefixEntry -> jwt.hasClaim(claimPrefixEntry.getKey()))
                .flatMap(entry -> this.extractAuthorities(jwt, entry.getKey(), entry.getValue()))
                .collectList()
                .map(authorities -> createAuthenticationToken(jwt, authorities));
    }

    protected AbstractAuthenticationToken createAuthenticationToken(Jwt jwt, List<GrantedAuthority> authorities) {
        return new JwtAuthenticationToken(jwt, authorities);
    }

    protected CoreDefaultConverter addMapping(String claimName, String prefix) {
        this.authoritiesMap.put(claimName, prefix);
        return this;
    }

    protected CoreDefaultConverter setMappings(HashMap<String, String> authoritiesMap) {
        this.authoritiesMap = authoritiesMap;
        return this;
    }

    protected Flux<GrantedAuthority> extractAuthorities(Jwt jwt, String claimName, String prefix) {
        Object claim = jwt.getClaim(claimName);

        if (claim instanceof String) {
            return Flux.fromArray(((String) claim).split(" "))
                    .map(authority -> new SimpleGrantedAuthority(prefix + authority));
        } else if (claim instanceof Collection<?>) {
            return Flux.fromIterable((Collection<?>) claim)
                    .filter(item -> item instanceof String)
                    .map(item -> new SimpleGrantedAuthority(prefix + item));
        }

        return Flux.empty();
    }

}
