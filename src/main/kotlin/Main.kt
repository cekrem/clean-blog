package io.github.cekrem

import io.github.cekrem.content.ContentType
import io.github.cekrem.content.internal.createMockGateway
import io.github.cekrem.content.usecase.GetContentTypesUseCaseImpl
import io.github.cekrem.content.usecase.GetContentUseCaseImpl
import io.github.cekrem.content.usecase.ListContentsByTypeUseCaseImpl
import io.github.cekrem.web.ServerConfig
import io.github.cekrem.web.startServer

fun main(args: Array<String>) {
    val debug = args.contains("--debug")

    val contentGateway =
        createMockGateway(
            contentTypes = setOf(ContentType(name = "posts", listable = true)),
        )

    // Create use cases
    val getContent = GetContentUseCaseImpl(contentGateway)
    val listContents = ListContentsByTypeUseCaseImpl(contentGateway)
    val getContentTypes = GetContentTypesUseCaseImpl(contentGateway)

    val serverConfig =
        ServerConfig(
            port = 8080,
            debug = debug,
        )

    startServer(getContent, listContents, getContentTypes, serverConfig)
}
