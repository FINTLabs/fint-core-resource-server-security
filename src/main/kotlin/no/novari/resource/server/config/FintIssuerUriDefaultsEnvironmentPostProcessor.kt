package no.novari.resource.server.config

import org.springframework.boot.SpringApplication
import org.springframework.boot.env.EnvironmentPostProcessor
import org.springframework.core.env.ConfigurableEnvironment
import org.springframework.core.env.MapPropertySource

class FintIssuerUriDefaultsEnvironmentPostProcessor : EnvironmentPostProcessor {

    override fun postProcessEnvironment(environment: ConfigurableEnvironment, application: SpringApplication) {
        val defaults = mapOf<String, Any>(
            "spring.security.oauth2.resourceserver.jwt.issuer-uri" to "https://idp.felleskomponent.no/nidp/oauth/nam"
        )
        environment.propertySources.addLast(MapPropertySource(PROPERTY_SOURCE_NAME, defaults))
    }

    private companion object {
        const val PROPERTY_SOURCE_NAME = "fint-core-security-defaults"
    }
}
