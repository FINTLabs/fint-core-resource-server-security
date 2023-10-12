package no.fintlabs.resourceServer.security.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class FintSecurity {

    @Value("${fint.security.enabled:true}")
    private boolean enabled;

    @Value("${fint.security.orgid:true}")
    private boolean orgIdRequired;

    @Value("${fint.security.component:true}")
    private boolean componentRequired;
}
