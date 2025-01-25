package io.github.cekrem.web

import io.github.cekrem.content.ContentGateway
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

data class WebServerConfig(
    val port: Int = 8080,
    val debug: Boolean = false,
    val host: String = "0.0.0.0",
)

class WebServer(
    private val gateway: ContentGateway,
    private val config: WebServerConfig = WebServerConfig(),
) {
    fun start() {
        if (config.debug) {
            System.setProperty("io.ktor.development", "true")
        }

        embeddedServer(
            Netty,
            port = config.port,
            host = config.host,
        ) {
            configure()

            val routes = Routes(gateway)
            routing { routes.apply { configureRoutes() } }
        }.start(wait = true)
    }
}
