package no.fintlabs.core.resource.server.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.core.resource.server.security.CorePrincipal;
import no.fintlabs.core.resource.server.security.CorePrincipalConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final FintSecurity fintSecurity;
    private final ConsumerConfig consumerConfig;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return fintSecurity.isEnabled() ? requireJwt(http) : permitAll(http);
    }

    private SecurityWebFilterChain requireJwt(ServerHttpSecurity http) {
        http
                .oauth2ResourceServer(oauth2ResourceServerSpec ->
                        oauth2ResourceServerSpec.jwt(
                                jwtSpec -> jwtSpec.jwtAuthenticationConverter(new CorePrincipalConverter())
                        )
                )
                .authorizeExchange(authorizeExchangeSpec ->
                        authorizeExchangeSpec
                                .anyExchange()
                                .access(this::checkCorePrincipalForAccess));

        return http.build();
    }

    private Mono<AuthorizationDecision> checkCorePrincipalForAccess(Mono<Authentication> authentication,
                                                                    AuthorizationContext context) {
        return authentication.map(auth -> {
            if (auth instanceof CorePrincipal corePrincipal) {
                boolean componentRequired = fintSecurity.isComponentRequired();
                boolean orgIdRequired = fintSecurity.isOrgIdRequired();
                boolean scopeRequired = fintSecurity.isScopeRequired();

                if (!componentRequired && !orgIdRequired && !scopeRequired) {
                    return new AuthorizationDecision(true);
                }

                boolean isComponentValid = validateComponent(corePrincipal, componentRequired);
                debugLogIfValidationFails("Component", isComponentValid);

                boolean isOrgIdValid = validateOrgId(corePrincipal, orgIdRequired);
                debugLogIfValidationFails("ORG ID", isOrgIdValid);

                boolean isScopeValid = validateScope(corePrincipal, scopeRequired);
                debugLogIfValidationFails("Scope", isScopeValid);

                return new AuthorizationDecision(isComponentValid && isOrgIdValid && isScopeValid);
            }

            log.warn("Jwt is not a CorePrincipal! Denying Request!");
            return new AuthorizationDecision(false);
        });
    }

    private void debugLogIfValidationFails(String validationType, boolean isValid) {
        if (!isValid) {
            log.debug("{} validation failed!", validationType);
        }
    }

    private boolean validateScope(CorePrincipal corePrincipal, boolean scopeRequired) {
        return !scopeRequired || corePrincipal.hasScope(getScope());
    }

    private boolean validateComponent(CorePrincipal corePrincipal, boolean componentRequired) {
        return !componentRequired || corePrincipal.hasRole(getComponentRole());
    }

    private boolean validateOrgId(CorePrincipal corePrincipal, boolean orgIdRequired) {
        return !orgIdRequired || corePrincipal.getOrgId().equals(consumerConfig.getOrgId());
    }

    private String getScope() {
        return String.format("fint-%s", fintSecurity.getRoleType().toLowerCase());
    }

    private String getComponentRole() {
        return String.format("FINT_%s_%s", fintSecurity.getRoleType(), consumerConfig.getComponent());
    }

    private SecurityWebFilterChain permitAll(ServerHttpSecurity http) {
        return permitAllExchanges(http).build();
    }

    private ServerHttpSecurity permitAllExchanges(ServerHttpSecurity http) {
        return http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());
    }

}
