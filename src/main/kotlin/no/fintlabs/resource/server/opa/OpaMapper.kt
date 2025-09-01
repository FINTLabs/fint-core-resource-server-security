package no.fintlabs.resource.server.opa

import no.fintlabs.resource.server.config.OpaProperties
import no.fintlabs.resource.server.opa.model.OpaRequest
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Component

@Component
class OpaMapper(
    private val opaProperties: OpaProperties
) {

    fun createOpaRequest(jwt: Jwt, req: ServerHttpRequest) =
        OpaRequest(
            username = getUsername(jwt),
            env = getEnv(req),
            domainName = getDomainName(req),
            packageName = getPackageName(req),
            resourceName = getResourceName(req)
        )

    private fun getUsername(jwt: Jwt): String =
        jwt.getClaimAsString("cn")

    private fun getEnv(req: ServerHttpRequest): String =
        if (opaProperties.envHeader)
            req.headers.getFirst("x-opa-env")
                ?.takeIf(String::isNotBlank)
                ?: error("Missing X-Opa-Env header")
        else req.uri.host.substringBefore('.')

    private fun getDomainName(req: ServerHttpRequest) =
        req.uri.path.segment(0) ?: error("Missing domain segment")

    private fun getPackageName(req: ServerHttpRequest) =
        req.uri.path.segment(1) ?: error("Missing package segment")

    private fun getResourceName(req: ServerHttpRequest) =
        req.uri.path.segmentOrNull(2)

    private fun String.segments() = split('/').filter(String::isNotBlank)
    private fun String.segment(index: Int) = segments().getOrNull(index)
    private fun String.segmentOrNull(index: Int) = segments().getOrNull(index)


}