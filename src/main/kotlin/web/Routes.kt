package io.github.cekrem.web

import io.github.cekrem.content.ContentGateway
import io.github.cekrem.web.dto.ContentDto
import io.github.cekrem.web.dto.ContentSummaryDto
import io.github.cekrem.web.dto.ContentTypeDto
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

class Routes(
    private val contentGateway: ContentGateway,
) {
    fun Route.configureRoutes() {
        get("/health") {
            call.respondText("OK")
        }

        // HTML Routes
        get("/") {
            val content =
                contentGateway.getByPath("pages/index")
                    ?: return@get call.respond(HttpStatusCode.NotFound)
            // TODO: Render with Mustache
            call.respond(content)
        }

        contentGateway.getContentTypes().filter { it.listable }.forEach { type ->
            get("/${type.name}") {
                val contents = contentGateway.getSummariesByType(type)
                // TODO: Render with Mustache
                call.respond(contents)
            }
        }

        get("/{type}/{slug}") {
            val type = call.parameters["type"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val slug = call.parameters["slug"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val content =
                contentGateway.getByPath("$type/$slug")
                    ?: return@get call.respond(HttpStatusCode.NotFound)
            // TODO: Render with Mustache
            call.respond(content)
        }

        // API Routes
        route("/api") {
            get("/") {
                val content =
                    contentGateway
                        .getByPath("pages/index")
                        ?.let { ContentDto.from(it) }
                        ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(content)
            }

            get("/contentTypes") {
                val types =
                    contentGateway
                        .getContentTypes()
                        .map { ContentTypeDto.from(it) }
                call.respond(types)
            }

            contentGateway.getContentTypes().filter { it.listable }.forEach { type ->
                get("/${type.name}") {
                    val contents =
                        contentGateway
                            .getSummariesByType(type)
                            .map { ContentSummaryDto.from(it) }
                    call.respond(contents)
                }
            }

            get("/{type}/{slug}") {
                val type = call.parameters["type"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val slug = call.parameters["slug"] ?: return@get call.respond(HttpStatusCode.BadRequest)
                val content =
                    contentGateway
                        .getByPath("$type/$slug")
                        ?.let { ContentDto.from(it) }
                        ?: return@get call.respond(HttpStatusCode.NotFound)
                call.respond(content)
            }
        }
    }
}
