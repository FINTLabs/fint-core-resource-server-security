package no.novari.resource.server.enums

enum class FintType {
    CLIENT,
    ADAPTER;

    companion object {
        fun fromUsername(username: String): FintType =
            valueOf(username.substringAfter('@').substringBefore('.').uppercase())
    }
}
