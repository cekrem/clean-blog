package infrastructure.web.internal

import application.usecase.GetContentUseCase
import application.usecase.GetListableContentTypes
import application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.infrastructure.web.internal.controller.ContentController
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import kotlinx.coroutines.runBlocking

internal class Routes(
    private val contentController: ContentController,
) {
    fun Route.configureRoutes() {
        val listableContentTypes = runBlocking {
            contentController.getListableContentTypes()
        }

        get("/health") {
            contentController.handleHealthCheck(call)
        }

        // HTML Routes
        get("/") {
            contentController.handleIndex(call)
        }

        listableContentTypes.forEach { type ->
            get("/${type.name}") {
                contentController.handleListContents(call, type)
            }
        }

        get("/{type}/{slug}") {
            val type = call.parameters["type"] ?: return@get call.respond(HttpStatusCode.BadRequest)
            val slug = call.parameters["slug"] ?: return@get call.respond(HttpStatusCode.BadRequest)

            contentController.handleGetContent(call, "$type/$slug")
        }
    }
}
