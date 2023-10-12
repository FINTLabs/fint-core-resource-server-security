package no.fintlabs.resourceServer.security.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("fint.security")
public class FintSecurity {

    private boolean enabled;

    @Value("${fint.security.orgid}")
    private boolean orgIdRequired;

    @Value("${fint.security.component}")
    private boolean componentRequired;

}
