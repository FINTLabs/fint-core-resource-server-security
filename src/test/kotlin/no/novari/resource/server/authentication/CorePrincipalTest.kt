package no.novari.resource.server.authentication

import io.mockk.every
import io.mockk.mockk
import no.novari.resource.server.JwtClaimsConstants.FINT_ASSET_IDS
import no.novari.resource.server.JwtClaimsConstants.ROLES
import no.novari.resource.server.JwtClaimsConstants.SCOPE
import no.novari.resource.server.JwtClaimsConstants.USERNAME
import no.novari.resource.server.enums.FintScope
import no.novari.resource.server.enums.FintType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.security.oauth2.jwt.Jwt

@DisplayName("CorePrincipal")
internal class CorePrincipalTest {

    private fun jwt(
        assetIds: String? = "fintlabs-no",
        username: String = "user@client.fintlabs.no",
        scopes: List<String>? = listOf("fint-client"),
        roles: List<String>? = null
    ) = mockk<Jwt>(relaxed = true).apply {
        every { getClaimAsString(FINT_ASSET_IDS) } returns assetIds
        every { getClaimAsString(USERNAME) } returns username
        every { getClaimAsStringList(SCOPE) } returns scopes
        every { getClaimAsStringList(ROLES) } returns roles
    }

    @Nested
    @DisplayName("assets")
    inner class Assets {
        @Test
        fun `splits comma separated asset claim into set`() {
            val principal = CorePrincipal(jwt(assetIds = "fintlabs-no,other-org"), emptyList())
            assertThat(principal.assets).containsExactlyInAnyOrder("fintlabs-no", "other-org")
        }
    }

    @Nested
    @DisplayName("orgId")
    inner class OrgId {
        @Test
        fun `replaces hyphens with dots in first asset`() {
            val principal = CorePrincipal(jwt(assetIds = "fintlabs-no,other-org"), emptyList())
            assertThat(principal.orgId).isEqualTo("fintlabs.no")
        }
    }

    @Nested
    @DisplayName("type")
    inner class Type {
        @Test
        fun `resolves CLIENT from username`() {
            val principal = CorePrincipal(jwt(username = "user@client.fintlabs.no"), emptyList())
            assertThat(principal.type).isEqualTo(FintType.CLIENT)
        }

        @Test
        fun `resolves ADAPTER from username`() {
            val principal = CorePrincipal(jwt(username = "user@adapter.fintlabs.no"), emptyList())
            assertThat(principal.type).isEqualTo(FintType.ADAPTER)
        }
    }

    @Nested
    @DisplayName("scopes")
    inner class Scopes {
        @Test
        fun `maps known claim values to FintScope entries`() {
            val principal = CorePrincipal(jwt(scopes = listOf("fint-client", "fint-adapter")), emptyList())
            assertThat(principal.scopes).containsExactlyInAnyOrder(FintScope.FINT_CLIENT, FintScope.FINT_ADAPTER)
        }

        @Test
        fun `ignores unknown scope values`() {
            val principal = CorePrincipal(jwt(scopes = listOf("fint-client", "something-else")), emptyList())
            assertThat(principal.scopes).containsExactly(FintScope.FINT_CLIENT)
        }

        @Test
        fun `is empty when claim is absent`() {
            val principal = CorePrincipal(jwt(scopes = null), emptyList())
            assertThat(principal.scopes).isEmpty()
        }
    }

    @Nested
    @DisplayName("components")
    inner class Components {
        @Test
        fun `strips FINT_Client_ prefix and keeps snake_case entries`() {
            val principal = CorePrincipal(
                jwt(roles = listOf("FINT_Client_utdanning_elev", "FINT_Client_administrasjon_fullmakt")),
                emptyList()
            )
            assertThat(principal.components)
                .containsExactlyInAnyOrder("utdanning_elev", "administrasjon_fullmakt")
        }

        @Test
        fun `strips FINT_Adapter_ prefix and keeps snake_case entries`() {
            val principal = CorePrincipal(
                jwt(roles = listOf("FINT_Adapter_utdanning_elev")),
                emptyList()
            )
            assertThat(principal.components).containsExactly("utdanning_elev")
        }

        @Test
        fun `drops CamelCase duplicates`() {
            val principal = CorePrincipal(
                jwt(roles = listOf("FINT_Client_utdanning_elev", "FINT_Client_UtdanningElev")),
                emptyList()
            )
            assertThat(principal.components).containsExactly("utdanning_elev")
        }

        @Test
        fun `drops entries without exactly one underscore after stripping`() {
            val principal = CorePrincipal(
                jwt(roles = listOf("FINT_Client_vigokodeverk", "authenticated", "FINT_Client_a_b_c")),
                emptyList()
            )
            assertThat(principal.components).isEmpty()
        }

        @Test
        fun `is empty when Roles claim is absent`() {
            val principal = CorePrincipal(jwt(roles = null), emptyList())
            assertThat(principal.components).isEmpty()
        }
    }

    @Nested
    @DisplayName("hasComponent")
    inner class HasComponent {
        @Test
        fun `returns true when domain and package match a component`() {
            val principal = CorePrincipal(
                jwt(roles = listOf("FINT_Client_utdanning_elev")),
                emptyList()
            )
            assertThat(principal.hasComponent("utdanning", "elev")).isTrue()
        }

        @Test
        fun `returns false when domain and package do not match any component`() {
            val principal = CorePrincipal(
                jwt(roles = listOf("FINT_Client_utdanning_elev")),
                emptyList()
            )
            assertThat(principal.hasComponent("administrasjon", "fullmakt")).isFalse()
        }
    }
}
