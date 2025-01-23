package io.github.cekrem

import io.github.cekrem.content.ContentGateway
import io.github.cekrem.content.createRssGateway
import io.github.cekrem.web.Routes
import io.ktor.server.application.*
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

fun Application.setupDependencyInjection() {
    install(Koin) {
        slf4jLogger()

        modules(
            module {
                single<ContentGateway> {
                    createRssGateway("https://example.com/feed.xml")
                }

                single {
                    Routes(get())
                }
            },
        )
    }
}
