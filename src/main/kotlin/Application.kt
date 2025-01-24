package io.github.cekrem

import io.github.cekrem.content.ContentType
import io.github.cekrem.content.createMockGateway
import io.github.cekrem.web.Routes
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain
        .main(args)
}

fun Application.module() {
    configure()

    // Manual DI
    val contentGateway =
        createMockGateway(
            contentTypes = setOf(ContentType(name = "posts", listable = true)),
        )
    val routes = Routes(contentGateway)

    // Configure routing
    routing { routes.apply { configureRoutes() } }
}
