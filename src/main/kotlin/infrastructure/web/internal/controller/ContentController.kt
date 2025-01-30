package io.github.cekrem.infrastructure.web.internal.controller

import application.usecase.GetContentUseCase
import application.usecase.GetListableContentTypes
import application.usecase.ListContentsByTypeUseCase
import domain.model.ContentType
import infrastructure.web.internal.template.toTemplateData
import io.github.cekrem.infrastructure.web.internal.template.toTemplateData
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.mustache.MustacheContent
import io.ktor.server.response.respond
import io.ktor.server.response.respondText

class ContentController(
    private val getContent: GetContentUseCase,
    private val listContents: ListContentsByTypeUseCase,
    private val getListableContentTypes: GetListableContentTypes
) {
    suspend fun getListableContentTypes(): Set<ContentType> {
        return getListableContentTypes(Unit)
    }

    suspend fun handleHealthCheck(call: ApplicationCall) {
        call.respondText("OK")
    }

    suspend fun handleGetContent(call: ApplicationCall, path: String) {
        val content =
            getContent(path)

        if (content == null) {
            call.response.status(HttpStatusCode.NotFound)
            return
        }

        call.respond(MustacheContent("content.mustache", content.toTemplateData()))
    }

    suspend fun handleIndex(call: ApplicationCall) {
        // TODO: support custom index page?
        handleGetContent(call, "pages/index")
    }

    suspend fun handleListContents(call: ApplicationCall, type: ContentType) {
        val list = listContents(type)
        call.respond(MustacheContent("list.mustache", list.toTemplateData()))
    }
}