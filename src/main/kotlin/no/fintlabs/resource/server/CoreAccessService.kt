package no.fintlabs.resource.server

import no.fintlabs.resource.server.authentication.CorePrincipal
import no.fintlabs.resource.server.config.SecurityProperties
import no.fintlabs.resource.server.enums.FintType
import no.fintlabs.resource.server.opa.OpaService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Service
class CoreAccessService(
    private val securityProperties: SecurityProperties,
    private val opaService: OpaService
) {

    private val logger: Logger = LoggerFactory.getLogger(CoreAccessService::class.java)

    fun authorizeCore(principal: CorePrincipal, exchange: ServerWebExchange): Mono<Boolean> =
        when {
            !matchesType(principal) -> denyType(principal)
            !matchesScope(principal) -> denyScope(principal)
            !matchesRole(principal, exchange) -> denyRole(principal, exchange)
            else -> checkOpa(principal, exchange)
        }

    private fun denyType(p: CorePrincipal): Mono<Boolean> {
        logger.debug(
            "Authorization failed: typeMatches=false, requiredType={}, principal={}",
            securityProperties.fintType, p
        )
        return Mono.just(false)
    }

    private fun denyScope(p: CorePrincipal): Mono<Boolean> {
        logger.debug(
            "Authorization failed: scopeMatches=false, requiredScopes={}, principalScopes={}",
            securityProperties.requiredScopes, p.scopes
        )
        return Mono.just(false)
    }

    private fun denyRole(p: CorePrincipal, ex: ServerWebExchange): Mono<Boolean> {
        logger.debug(
            "Authorization failed: roleMatches=false for path={}, principalRoles={}",
            ex.request.uri.path, p.roles
        )
        return Mono.just(false)
    }

    private fun matchesType(p: CorePrincipal): Boolean =
        securityProperties.fintType
            ?.let { if (it == FintType.CLIENT) p.isClient() else p.isAdapter() }
            ?: true

    private fun matchesScope(p: CorePrincipal): Boolean =
        securityProperties.requiredScopes
            ?.any { p.scopes.contains(it.formattedValue) }
            ?: true

    private fun matchesRole(p: CorePrincipal, ex: ServerWebExchange): Boolean {
        val segments = ex.request.uri.path
            .split('/')
            .filter(String::isNotBlank)
        val domain = segments.getOrNull(0) ?: return false
        val pkg = segments.getOrNull(1) ?: return false
        return p.hasRole(domain, pkg)
    }

    private fun checkOpa(principal: CorePrincipal, ex: ServerWebExchange): Mono<Boolean> =
        opaService.requestOpa(principal.token, ex.request)
            .map { opa ->
                ex.attributes["x-opa-fields"] = opa.result.fields
                ex.attributes["x-opa-relations"] = opa.result.relations
                opa.result.allow
            }
}