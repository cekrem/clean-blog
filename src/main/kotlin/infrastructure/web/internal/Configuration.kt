package infrastructure.web.internal

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.http.CacheControl
import io.ktor.http.ContentType
import io.ktor.http.content.CachingOptions
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.mustache.Mustache
import io.ktor.server.plugins.autohead.AutoHeadResponse
import io.ktor.server.plugins.cachingheaders.CachingHeaders
import io.ktor.server.plugins.compression.Compression
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.defaultheaders.DefaultHeaders

internal fun Application.configure() {
    // Templating
    install(Mustache) { mustacheFactory = DefaultMustacheFactory("templates") }

    // HTTP Features
    install(DefaultHeaders) { header("X-Engine", "Ktor") }

    install(AutoHeadResponse)
    install(Compression)
    install(CachingHeaders) {
        options { _, outgoingContent ->
            when (outgoingContent.contentType?.withoutParameters()) {
                ContentType.Text.CSS ->
                    CachingOptions(CacheControl.MaxAge(maxAgeSeconds = 24 * 60 * 60))

                else -> null
            }
        }
    }

    install(ContentNegotiation) {
        json()
    }
}
