package io.github.cekrem.adapter.controller

import io.github.cekrem.adapter.presenter.ContentPresenter
import io.github.cekrem.application.usecase.GetContentUseCase
import io.github.cekrem.application.usecase.GetListableContentTypes
import io.github.cekrem.application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.domain.model.ContentType

class ContentController(
    private val getContent: GetContentUseCase,
    private val listContents: ListContentsByTypeUseCase,
    private val getListableContentTypes: GetListableContentTypes,
    private val contentPresenter: ContentPresenter,
) {
    suspend fun getListableContentTypes(): Set<ContentType> = getListableContentTypes(Unit)

    suspend fun healthCheckResponse() = Response(statusCode = 200, body = "OK")

    suspend fun getContentResponse(
        type: String?,
        slug: String?,
    ): Response<Any> {
        if (type == null || slug == null) {
            return Response(statusCode = 404)
        }

        val path = "$type/$slug"

        val content =
            getContent(path) ?: return Response(statusCode = 404)

        return Response(statusCode = 200, body = contentPresenter.presentContent(content))
    }

    // TODO: support custom index page somehow?
    suspend fun getIndexResponse() = getContentResponse("pages", "index")

    suspend fun getListContentsResponse(type: ContentType): Response<Any> {
        val list = listContents(type)
        return Response(200, contentPresenter.presentContentList(list))
    }

    data class Response<T>(
        val statusCode: Int,
        val body: T? = null,
    )
}
