package io.github.cekrem

import io.github.cekrem.content.ContentType
import io.github.cekrem.content.createMockGateway
import io.github.cekrem.content.usecase.GetContent
import io.github.cekrem.content.usecase.GetContentTypes
import io.github.cekrem.content.usecase.ListContentsByType
import io.github.cekrem.web.StartWebServer
import io.github.cekrem.web.WebServerConfig

fun main(args: Array<String>) {
    val debug = args.contains("--debug")

    val contentGateway =
        createMockGateway(
            contentTypes = setOf(ContentType(name = "posts", listable = true)),
        )

    // Create use cases
    val getContent = GetContent(contentGateway)
    val listContents = ListContentsByType(contentGateway)
    val getContentTypes = GetContentTypes(contentGateway)

    val config =
        WebServerConfig(
            port = 8080,
            debug = debug,
        )

    StartWebServer(getContent, listContents, getContentTypes)(config)
}
