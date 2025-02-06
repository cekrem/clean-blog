package infrastructure.web

import application.usecase.GetContentUseCase
import application.usecase.GetListableContentTypes
import application.usecase.ListContentsByTypeUseCase
import infrastructure.web.internal.Routes
import infrastructure.web.internal.configure
import interfaceadapters.presenter.ContentPresenter
import io.github.cekrem.infrastructure.web.internal.controller.ContentController
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
        val contentController = ContentController(
            getContent = getContent,
            listContents = listContents,
            getListableContentTypes = getListableContentTypes,
            contentPresenter = contentPresenter,
        )

        val routes = Routes(contentController)

        routing { routes.apply { configureRoutes() } }
    }.start(wait = true)
}
