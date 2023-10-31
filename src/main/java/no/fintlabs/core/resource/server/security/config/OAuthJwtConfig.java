package no.fintlabs.core.resource.server.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


/**
 * Retrieves the JWT Issuer URI for the application.
 * Eliminates the necessity for apps to configure this separately.
 */
@Configuration
@PropertySource(value = "classpath:oauth-jwt-settings/jwt-config.properties")
public class OAuthJwtConfig {
}
