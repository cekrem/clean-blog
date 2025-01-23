package io.github.cekrem

import io.github.cekrem.content.ContentStorage
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
                    single<ContentStorage> { TODO() }

                    single {
                        val contentStorage = get<ContentStorage>()
                        Routes(contentStorage)
                    }
                }
        )
    }
}
