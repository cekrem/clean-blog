package io.github.cekrem.web

import io.github.cekrem.content.usecase.GetContent
import io.github.cekrem.content.usecase.GetContentTypes
import io.github.cekrem.content.usecase.ListContentsByType
import io.github.cekrem.usecase.NoOutputUseCase
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*

data class WebServerConfig(
    val port: Int = 8080,
    val debug: Boolean = false,
    val host: String = "0.0.0.0",
)

class StartWebServer(
    private val getContent: GetContent,
    private val listContents: ListContentsByType,
    private val getContentTypes: GetContentTypes,
) : NoOutputUseCase<WebServerConfig> {
    override fun execute(config: WebServerConfig) {
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
}
