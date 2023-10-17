package no.fintlabs.core.resource.server.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource(value = "classpath:oauth-jwt-settings/jwt-config.properties")
public class OAuthJwtConfig {
}
