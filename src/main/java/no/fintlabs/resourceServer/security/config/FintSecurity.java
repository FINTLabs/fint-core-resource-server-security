package no.fintlabs.resourceServer.security.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("fint.security")
public class FintSecurity {
}
