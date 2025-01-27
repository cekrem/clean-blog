package io.github.cekrem.web

import io.github.cekrem.usecase.UseCase
import io.github.cekrem.web.dto.ContentDto
import io.github.cekrem.web.dto.ContentSummaryDto
import io.github.cekrem.web.dto.ContentTypeDto
import io.github.cekrem.content.Content
import io.github.cekrem.content.ContentType
import io.github.cekrem.content.ContentSummary
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

class Routes(
    private val getContent: UseCase<String, Content?>,
    private val listContents: UseCase<ContentType, List<ContentSummary>>,
    private val getContentTypes: UseCase<Unit, Set<ContentType>>,
) {
    fun Route.configureRoutes() {
        get("/health") {
            call.respondText("OK")
        }

        // HTML Routes
        get("/") {
            val content =
                getContent("pages/index")
                    ?: return@get call.respond(HttpStatusCode.NotFound)
            // TODO: Render with Mustache
            call.respond(content)
        }

        getContentTypes(Unit).filter { it.listable }.forEach { type ->
            get("/${type.name}") {
                val contents = listContents(type)
                // TODO: Render with Mustache
                call.respond(contents)
            }
        }

        get("/{type}/{slug}") {
            val type = call.parameters["type"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val slug = call.parameters["slug"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val content =
                getContent("$type/$slug")
                    ?: return@get call.respond(HttpStatusCode.NotFound)
            // TODO: Render with Mustache
            call.respond(content)
        }

        // API Routes
        route("/api") {
            get("/types") {
                val types =
                    getContentTypes(Unit)
                        .map(ContentTypeDto::from)
                call.respond(types)
            }

            getContentTypes(Unit).filter { it.listable }.forEach { type ->
                get("/${type.name}") {
                    val contents =
                        listContents(type)
                            .map(ContentSummaryDto::from)
                    call.respond(contents)
                }

                get("/${type.name}/{slug}") {
                    val slug =
                        call.parameters["slug"]
                            ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val content =
                        getContent("${type.name}/$slug")
                            ?.let(ContentDto::from)
                            ?: return@get call.respond(HttpStatusCode.NotFound)
                    call.respond(content)
                }
            }
        }
    }
}
