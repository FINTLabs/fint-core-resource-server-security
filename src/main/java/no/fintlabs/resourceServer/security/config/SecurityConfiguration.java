package no.fintlabs.resourceServer.security.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.fintlabs.resourceServer.security.CorePrincipal;
import no.fintlabs.resourceServer.security.CorePrincipalConverter;
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

                if (!componentRequired && !orgIdRequired) {
                    return new AuthorizationDecision(true);
                }

                boolean isComponentValid = !componentRequired || corePrincipal.hasRole(getComponentRole());
                boolean isOrgIdValid = !orgIdRequired || corePrincipal.orgIdsMatch(consumerConfig.getOrgId());

                return new AuthorizationDecision(isComponentValid && isOrgIdValid);
            }

            log.warn("(SecurityConfiguration): Jwt is not a CorePrincipal! ");
            return new AuthorizationDecision(false);
        });
    }

    private String getComponentRole() {
        return String.format("FINT_Client_%s", consumerConfig.getComponent());
    }

    private SecurityWebFilterChain permitAll(ServerHttpSecurity http) {
        return permitAllExchanges(http).build();
    }

    private ServerHttpSecurity permitAllExchanges(ServerHttpSecurity http) {
        return http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());
    }

}
