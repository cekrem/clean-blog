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

fun startServer(
    getContent: GetContentUseCase,
    listContents: ListContentsByTypeUseCase,
    getListableContentTypes: GetListableContentTypes,
    contentPresenter: ContentPresenter,
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
        val contentController =
            ContentController(
                getContent = getContent,
                listContents = listContents,
                getListableContentTypes = getListableContentTypes,
                contentPresenter = contentPresenter,
            )

        val routes = Routes(contentController)

        routing { routes.apply { configureRoutes() } }
    }.start(wait = true)
}
