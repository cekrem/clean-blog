package io.github.cekrem

import domain.model.Content
import domain.model.ContentType
import domain.model.Metadata
import infrastructure.factory.createMockGateway
import infrastructure.usecase.GetContentUseCaseImpl
import infrastructure.usecase.GetListableContentTypesImpl
import infrastructure.usecase.ListContentsByTypeUseCaseImpl
import infrastructure.web.ServerConfig
import infrastructure.web.startServer

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
    val getContent = GetContentUseCaseImpl(contentSource)
    val listContents = ListContentsByTypeUseCaseImpl(contentSource)
    val getListableContentTypes = GetListableContentTypesImpl(contentSource)

    val serverConfig =
        ServerConfig(
            port = 8080,
            debug = debug,
        )

    startServer(getContent, listContents, getListableContentTypes, serverConfig)
}
