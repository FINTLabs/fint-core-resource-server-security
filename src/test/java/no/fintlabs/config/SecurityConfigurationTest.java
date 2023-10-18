package no.fintlabs.config;

import no.fintlabs.core.resource.server.security.config.FintConsumerConfig;
import no.fintlabs.core.resource.server.security.config.FintSecurity;
import no.fintlabs.core.resource.server.security.config.SecurityConfiguration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.config.web.server.ServerHttpSecurity;

class SecurityConfigurationTest {

    @InjectMocks
    private SecurityConfiguration securityConfiguration;

    @Mock
    private FintSecurity fintSecurity;

    @Mock
    private FintConsumerConfig consumerConfig;

    @Mock
    private ServerHttpSecurity http;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSecurityWebFilterChainEnabled() {
        // TODO: Set up your mocks to simulate the behavior when fintSecurity.isEnabled() returns true.
        // Call securityConfiguration.securityWebFilterChain(http)
        // Verify the expected behavior
    }

    @Test
    void testSecurityWebFilterChainDisabled() {
        // TODO: Set up your mocks to simulate the behavior when fintSecurity.isEnabled() returns false.
        // Call securityConfiguration.securityWebFilterChain(http)
        // Verify the expected behavior
    }

    @Test
    void testCheckCorePrincipalForAccess() {
        // TODO: Create various tests for this method based on the different conditions mentioned above.
    }

    @Test
    void testGetComponentRole() {
        // TODO: Test the getComponentRole method
    }

    @Test
    void testPermitAll() {
        // TODO: Test the permitAll method
    }

    // Add more tests as required
}
