package io.github.cekrem.web

import io.github.cekrem.content.Content
import io.github.cekrem.content.ContentStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class Routes(private val contentStorage: ContentStorage) {
    fun Route.configureRoutes() {
        // Home page
        get("/") {
            val content = contentStorage.getByPath("pages/index")
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(content)
        }

        // List content by type (e.g., /posts, /events)
        contentStorage.getContentTypes().filter { it.listable }.forEach { type ->
            get("/${type.name}") {
                val contents = contentStorage.getByType(type)
                call.respond(contents)
            }
        }

        // Individual content pages
        get("/{type}/{slug}") {
            val type = call.parameters["type"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val slug = call.parameters["slug"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val content = contentStorage.getByPath("$type/$slug")
                ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(content)
        }
    }
} 