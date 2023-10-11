package no.fintlabs.resourceServer.security.config;

import lombok.RequiredArgsConstructor;
import no.vigoiks.resourceserver.security.FintJwtDefaultConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final FintSecurity fintSecurity;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return fintSecurity.isEnabled() ? requireJwt(http) : permitAll(http);
    }

    private SecurityWebFilterChain requireJwt(ServerHttpSecurity http) {
        return http.oauth2ResourceServer(oAuth2ResourceServerSpec ->
                oAuth2ResourceServerSpec.jwt(jwtSpec ->
                        jwtSpec.jwtAuthenticationConverter(new FintJwtDefaultConverter())
                )
        ).build();
    }

    private SecurityWebFilterChain permitAll(ServerHttpSecurity http) {
        return permitAllExchanges(http).build();
    }

    private ServerHttpSecurity permitAllExchanges(ServerHttpSecurity http) {
        return http.authorizeExchange(authorizeExchangeSpec -> authorizeExchangeSpec.anyExchange().permitAll());
    }

}
