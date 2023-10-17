package no.fintlabs.core.resource.server.security.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class ConsumerConfig {

    @Value("${fint.consumer.domain:}")
    private String domain;

    @Value("${fint.consumer.package:}")
    private String packageName;

    @Value("${fint.consumer.orgId:}")
    private String orgId;

    public String getComponent() {
        return String.format("%s_%s", domain, packageName);
    }

}
