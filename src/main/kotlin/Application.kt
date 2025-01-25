package io.github.cekrem

import io.github.cekrem.content.ContentType
import io.github.cekrem.content.createMockGateway
import io.github.cekrem.web.WebServer
import io.github.cekrem.web.WebServerConfig

fun main(args: Array<String>) {
    val debug = args.contains("--debug")

    val contentGateway =
        createMockGateway(
            contentTypes = setOf(ContentType(name = "posts", listable = true)),
        )

    val config =
        WebServerConfig(
            port = 8080,
            debug = debug,
        )

    WebServer(contentGateway, config).start()
}
