package no.fintlabs.resource.server.opa

import kotlinx.coroutines.reactor.mono
import no.fintlabs.resource.server.config.OpaProperties
import no.fintlabs.resource.server.opa.model.OpaResponse
import no.fintlabs.resource.server.opa.model.OpaResult
import org.slf4j.LoggerFactory
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class OpaService(
    private val opaProperties: OpaProperties,
    private val opaMapper: OpaMapper,
    private val opaClient: OpaClient
) {

    private val logger = LoggerFactory.getLogger(javaClass)

    fun requestOpa(jwt: Jwt, request: ServerHttpRequest): Mono<OpaResponse> =
        takeIf { opaProperties.enabled }
            ?.let {
                val opaRequest = opaMapper.createOpaRequest(jwt, request)
                logger.debug("Performing request to Opa with this payload: {}", opaRequest)
                opaClient.getDecision(opaRequest)
            }
            ?: mono { acceptResponse() }

    private fun acceptResponse(): OpaResponse =
        OpaResponse(
            result = OpaResult(allow = true),
        )

}