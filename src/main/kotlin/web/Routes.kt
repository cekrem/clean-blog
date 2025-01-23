package io.github.cekrem.web

import io.github.cekrem.content.ContentGateway
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

class Routes(
    private val contentGateway: ContentGateway,
) {
    fun Route.configureRoutes() {
        // Home page
        get("/") {
            val content =
                contentGateway.getByPath("pages/index")
                    ?: return@get call.respond(HttpStatusCode.NotFound)
            call.respond(content)
        }

        // List content by type (e.g., /posts, /events)
        contentGateway.getContentTypes().filter { it.listable }.forEach { type ->
            get("/${type.name}") {
                val contents = contentGateway.getSummariesByType(type)
                // TODO: sort by published, and transform to some mustache stuff
                call.respond(contents)
            }
        }

        // Individual content pages
        get("/{type}/{slug}") {
            val type = call.parameters["type"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val slug = call.parameters["slug"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val content =
                contentGateway.getByPath("$type/$slug")
                    ?: return@get call.respond(HttpStatusCode.NotFound)

            // TODO: transform to some mustache stuff
            call.respond(content)
        }
    }
}
