package io.github.cekrem.web.internal

import io.github.cekrem.application.usecase.GetContentTypesUseCase
import io.github.cekrem.application.usecase.GetContentUseCase
import io.github.cekrem.application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.web.internal.response.ContentResponse
import io.github.cekrem.web.internal.response.ContentSummaryResponse
import io.github.cekrem.web.internal.response.ContentTypeResponse
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route

internal class Routes(
    private val getContent: GetContentUseCase,
    private val listContents: ListContentsByTypeUseCase,
    private val getContentTypes: GetContentTypesUseCase,
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
                        .map(ContentTypeResponse::from)
                call.respond(types)
            }

            getContentTypes(Unit).filter { it.listable }.forEach { type ->
                get("/${type.name}") {
                    val contents =
                        listContents(type)
                            .map(ContentSummaryResponse::from)
                    call.respond(contents)
                }

                get("/${type.name}/{slug}") {
                    val slug =
                        call.parameters["slug"]
                            ?: return@get call.respond(HttpStatusCode.BadRequest)
                    val content =
                        getContent("${type.name}/$slug")
                            ?.let(ContentResponse::from)
                            ?: return@get call.respond(HttpStatusCode.NotFound)
                    call.respond(content)
                }
            }
        }
    }
}
