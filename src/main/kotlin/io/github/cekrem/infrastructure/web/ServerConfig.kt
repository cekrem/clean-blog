package io.github.cekrem.infrastructure.web

data class ServerConfig(
    val port: Int = 8080,
    val debug: Boolean = false,
    val host: String = "0.0.0.0",
)
