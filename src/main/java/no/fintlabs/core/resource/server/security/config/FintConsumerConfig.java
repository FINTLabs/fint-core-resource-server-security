package no.fintlabs.core.resource.server.security.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class FintConsumerConfig {

    @Value("${fint.consumer.domain:}")
    private String domain;

    @Value("${fint.consumer.package:}")
    private String packageName;

    @Value("${fint.consumer.org-id:}")
    private String orgId;

    public String getComponent() {
        return String.format("%s_%s", domain, packageName);
    }

}
