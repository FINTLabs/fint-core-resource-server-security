package no.novari.resource.server.authentication

import no.novari.resource.server.JwtClaimsConstants.FINT_ASSET_IDS
import no.novari.resource.server.JwtClaimsConstants.SCOPE
import no.novari.resource.server.JwtClaimsConstants.USERNAME
import no.novari.resource.server.enums.FintScope
import no.novari.resource.server.enums.FintType
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken

class CorePrincipal(
    jwt: Jwt,
    authorities: Collection<GrantedAuthority>
) : JwtAuthenticationToken(jwt, authorities) {

    val username: String = jwt.getClaimAsString(USERNAME)

    val assets: Set<String> = jwt.getClaimAsString(FINT_ASSET_IDS)
        ?.split(",")
        ?.map(String::trim)
        ?.filter(String::isNotEmpty)
        ?.toSet()
        .orEmpty()

    val orgId: String = assets.first().replace("-", ".")

    val type: FintType = FintType.fromUsername(username)

    val scopes: Set<FintScope> = jwt.getClaimAsStringList(SCOPE)
        ?.mapNotNull(FintScope::fromClaim)
        ?.toSet()
        .orEmpty()
}
