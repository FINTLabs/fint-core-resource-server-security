package no.novari.resource.server.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.boot.Banner
import org.springframework.boot.SpringApplication
import org.springframework.boot.WebApplicationType
import org.springframework.context.annotation.Configuration

internal class FintIssuerUriDefaultsEnvironmentPostProcessorTest {

    @Test
    fun `loads default issuer through spring factories at startup`() {
        application().run().use { context ->
            assertThat(context.environment.getProperty(ISSUER_URI))
                .isEqualTo("https://idp.felleskomponent.no/nidp/oauth/nam")
        }
    }

    @Test
    fun `allows application configuration to override default issuer`() {
        application().run("--$ISSUER_URI=https://example.com/issuer").use { context ->
            assertThat(context.environment.getProperty(ISSUER_URI))
                .isEqualTo("https://example.com/issuer")
        }
    }

    private fun application() = SpringApplication(TestConfiguration::class.java).apply {
        setWebApplicationType(WebApplicationType.NONE)
        setBannerMode(Banner.Mode.OFF)
        setLogStartupInfo(false)
    }

    @Configuration(proxyBeanMethods = false)
    internal class TestConfiguration

    private companion object {
        const val ISSUER_URI = "spring.security.oauth2.resourceserver.jwt.issuer-uri"
    }
}
