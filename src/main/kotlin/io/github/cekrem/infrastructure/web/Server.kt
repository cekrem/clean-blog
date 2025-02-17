package io.github.cekrem.infrastructure.web

import io.github.cekrem.adapter.presenter.ContentPresenter
import io.github.cekrem.application.usecase.GetContentUseCase
import io.github.cekrem.application.usecase.GetListableContentTypes
import io.github.cekrem.application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.infrastructure.web.internal.Routes
import io.github.cekrem.infrastructure.web.internal.configure
import io.github.cekrem.infrastructure.web.internal.controller.ContentController
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.routing.routing
import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout

class Server(
    getContent: GetContentUseCase,
    listContents: ListContentsByTypeUseCase,
    getListableContentTypes: GetListableContentTypes,
    contentPresenter: ContentPresenter,
    config: ServerConfig = ServerConfig(),
) {
    private val embeddedServer =
        embeddedServer(
            Netty,
            port = config.port,
            host = config.host,
        ) {
            configure()
            val contentController =
                ContentController(
                    getContent = getContent,
                    listContents = listContents,
                    getListableContentTypes = getListableContentTypes,
                    contentPresenter = contentPresenter,
                )

            val routes = Routes(contentController)

            routing { routes.apply { configureRoutes() } }
        }

    fun runBlocking() = embeddedServer.start(true)

    fun start() {
        embeddedServer.start(wait = false)
    }

    fun stop() {
        embeddedServer.stop()
    }

    suspend fun ensureReady(
        timeoutMs: Long = 5000,
        retryIntervalMs: Long = 100,
    ) = withTimeout(timeoutMs) {
        while (!embeddedServer.engineConfig.connectors.all { it.port != -1 }) {
            delay(retryIntervalMs)
        }
    }
}
