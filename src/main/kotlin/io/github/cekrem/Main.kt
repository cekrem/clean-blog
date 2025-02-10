package io.github.cekrem

import io.github.cekrem.application.usecase.GetContentUseCase
import io.github.cekrem.application.usecase.GetListableContentTypes
import io.github.cekrem.application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
import io.github.cekrem.infrastructure.factory.createMockGateway
import io.github.cekrem.infrastructure.web.ServerConfig
import io.github.cekrem.infrastructure.web.internal.presenter.MustacheContentPresenter
import io.github.cekrem.infrastructure.web.startServer

fun main(args: Array<String>) {
    val debug = args.contains("--debug")

    val contentSource =
        createMockGateway(
            contentTypes = setOf(ContentType(name = "posts", listable = true)),
            contents =
                mapOf(
                    "pages/index" to
                        Content(
                            path = "pages/index",
                            title = "This is index!",
                            blocks = emptyList(),
                            type = ContentType(name = "pages", listable = false),
                            metadata = Metadata(),
                            slug = "pages/index",
                        ),
                ),
        )

    // Create use cases
    val getContent = GetContentUseCase(contentSource)
    val listContents = ListContentsByTypeUseCase(contentSource)
    val getListableContentTypes = GetListableContentTypes(contentSource)

    // Create content presenter
    val contentPresenter = MustacheContentPresenter()

    val serverConfig =
        ServerConfig(
            port = 8080,
            debug = debug,
        )

    startServer(
        getContent = getContent,
        listContents = listContents,
        getListableContentTypes = getListableContentTypes,
        contentPresenter = contentPresenter,
        config = serverConfig,
    )
}
