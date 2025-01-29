package io.github.cekrem.web

import io.github.cekrem.application.usecase.GetContentTypesUseCase
import io.github.cekrem.application.usecase.GetContentUseCase
import io.github.cekrem.application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.web.internal.Routes
import io.github.cekrem.web.internal.configure
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing

data class ServerConfig(
    val port: Int = 8080,
    val debug: Boolean = false,
    val host: String = "0.0.0.0",
)

fun startServer(
    getContent: GetContentUseCase,
    listContents: ListContentsByTypeUseCase,
    getContentTypes: GetContentTypesUseCase,
    config: ServerConfig = ServerConfig(),
) {
    if (config.debug) {
        System.setProperty("io.ktor.development", "true")
    }

    embeddedServer(
        Netty,
        port = config.port,
        host = config.host,
    ) {
        configure()

        val routes = Routes(getContent, listContents, getContentTypes)
        routing { routes.apply { configureRoutes() } }
    }.start(wait = true)
}
