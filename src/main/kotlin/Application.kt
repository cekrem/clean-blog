package io.github.cekrem

import io.github.cekrem.web.Routes
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configure()
    setupDependencyInjection()

    // Get Routes instance from Koin
    val routes by inject<Routes>()

    // Configure routing
    routing { routes.apply { configureRoutes() } }
}
