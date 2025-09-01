package no.fintlabs.resource.server.opa.model

import com.fasterxml.jackson.annotation.JsonInclude

data class OpaRequest(val input: OpaInput) {
    constructor(username: String, env: String, domainName: String, packageName: String, resourceName: String?) : this(
        OpaInput(
            username = username,
            env = env,
            domainName = domainName,
            packageName = packageName,
            resourceName = resourceName
        )
    )
}

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OpaInput(
    val username: String,
    val env: String,
    val domainName: String,
    val packageName: String,
    val resourceName: String?
)