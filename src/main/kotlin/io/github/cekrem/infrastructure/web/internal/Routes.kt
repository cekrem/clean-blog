package io.github.cekrem.infrastructure.web.internal

import io.github.cekrem.adapter.controller.ContentController
import io.ktor.http.HttpStatusCode
import io.ktor.server.response.respondNullable
import io.ktor.server.routing.Route
import io.ktor.server.routing.RoutingCall
import io.ktor.server.routing.get
import kotlinx.coroutines.runBlocking

internal class Routes(
    private val contentController: ContentController,
) {
    fun Route.configureRoutes() {
        val listableContentTypes =
            runBlocking {
                contentController.getListableContentTypes()
            }

        get("/health") {
            call.handleResponse(contentController.healthCheckResponse())
        }

        // HTML Routes
        get("/") {
            call.handleResponse(contentController.getIndexResponse())
        }

        listableContentTypes.forEach { type ->
            get("/${type.name}") {
                call.handleResponse(contentController.getListContentsResponse(type))
            }
        }

        get("/{type}/{slug}") {
            val type = call.parameters["type"]
            val slug = call.parameters["slug"]

            call.handleResponse(contentController.getContentResponse(type, slug))
        }
    }
}

private suspend inline fun <reified T> RoutingCall.handleResponse(response: ContentController.Response<T>) {
    respondNullable(status = HttpStatusCode(response.statusCode, ""), message = response.body)
}
