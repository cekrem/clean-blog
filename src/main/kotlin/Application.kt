package io.github.cekrem

import io.github.cekrem.content.ContentType
import io.github.cekrem.content.internal.createMockGateway
import io.github.cekrem.content.usecase.GetContentTypesUseCase
import io.github.cekrem.content.usecase.GetContentUseCase
import io.github.cekrem.content.usecase.ListContentsByTypeUseCase
import io.github.cekrem.web.ServerConfig
import io.github.cekrem.web.startServer

fun main(args: Array<String>) {
    val debug = args.contains("--debug")

    val contentGateway =
        createMockGateway(
            contentTypes = setOf(ContentType(name = "posts", listable = true)),
        )

    // Create use cases
    val getContent = GetContentUseCase.createFromContentGateway(contentGateway)
    val listContents = ListContentsByTypeUseCase.createFromContentGateway(contentGateway)
    val getContentTypes = GetContentTypesUseCase.createFromContentGateway(contentGateway)

    val serverConfig =
        ServerConfig(
            port = 8080,
            debug = debug,
        )

    startServer(getContent, listContents, getContentTypes, serverConfig)
}
