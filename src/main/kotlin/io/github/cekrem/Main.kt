package io.github.cekrem

import io.github.cekrem.adapter.controller.ContentController
import io.github.cekrem.application.usecase.GetContentUseCase
import io.github.cekrem.application.usecase.GetListableContentTypes
import io.github.cekrem.application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.infrastructure.factory.createFileGateway
import io.github.cekrem.infrastructure.web.Server
import io.github.cekrem.infrastructure.web.ServerConfig
import io.github.cekrem.infrastructure.web.internal.presenter.MustacheContentPresenter

fun main(args: Array<String>) {
    val debug = args.contains("--debug")

    val contentSource = createFileGateway("./content")

    // Create use cases
    val getContent = GetContentUseCase(contentSource)
    val listContents = ListContentsByTypeUseCase(contentSource)
    val getListableContentTypes = GetListableContentTypes(contentSource)

    // Create controller / presenter
    val contentPresenter = MustacheContentPresenter()
    val contentController =
        ContentController(
            getContent = getContent,
            listContents = listContents,
            getListableContentTypes = getListableContentTypes,
            contentPresenter = contentPresenter,
        )

    val serverConfig =
        ServerConfig(
            port = 8080,
            debug = debug,
        )

    Server(
        contentController = contentController,
        config = serverConfig,
    ).runBlocking()
}
