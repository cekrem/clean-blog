package io.github.cekrem

import com.github.mustachejava.DefaultMustacheFactory
import io.ktor.http.*
import io.ktor.http.content.CachingOptions
import io.ktor.server.application.*
import io.ktor.server.mustache.*
import io.ktor.server.plugins.autohead.*
import io.ktor.server.plugins.cachingheaders.*
import io.ktor.server.plugins.compression.*
import io.ktor.server.plugins.defaultheaders.*

fun Application.configure() {
    // Templating
    install(Mustache) { mustacheFactory = DefaultMustacheFactory("templates/mustache") }

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
}
