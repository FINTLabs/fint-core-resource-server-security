package no.novari.resource.server.enums

enum class FintScope(val claimValue: String) {
    FINT_CLIENT("fint-client"),
    FINT_ADAPTER("fint-adapter");

    companion object {
        private val byClaim = entries.associateBy(FintScope::claimValue)
        fun fromClaim(value: String): FintScope? = byClaim[value]
    }
}
