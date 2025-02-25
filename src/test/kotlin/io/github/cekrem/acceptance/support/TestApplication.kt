package io.github.cekrem.acceptance.support

import io.github.cekrem.adapter.controller.ContentController
import io.github.cekrem.application.usecase.GetContentUseCase
import io.github.cekrem.application.usecase.GetListableContentTypes
import io.github.cekrem.application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.infrastructure.contentsource.FileContentSource
import io.github.cekrem.infrastructure.parser.MarkdownContentParser
import io.github.cekrem.infrastructure.web.Server
import io.github.cekrem.infrastructure.web.ServerConfig
import io.github.cekrem.infrastructure.web.internal.presenter.MustacheContentPresenter
import kotlinx.coroutines.runBlocking
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText

class TestApplication private constructor() {
    private val tempDir: Path = Files.createTempDirectory("blog-test")
    private val server: Server
    val baseUrl: String
        get() = "http://localhost:${config.port}"

    private val config = ServerConfig(debug = true, port = 8081)

    init {
        server = createServer()
    }

    fun start() {
        server.start()
        runBlocking { server.ensureReady() }
    }

    fun stop() {
        server.stop()
        tempDir.toFile().deleteRecursively()
    }

    fun givenBlogPost(
        slug: String,
        content: String,
    ) {
        tempDir.resolve("posts").createDirectories()
        tempDir.resolve("posts/$slug.md").writeText(content)
    }

    private fun createServer(): Server {
        val contentSource =
            FileContentSource(
                contentRoot = tempDir.toString(),
                parser = MarkdownContentParser(),
                extension = "md",
            )

        val contentController =
            ContentController(
                getContent = GetContentUseCase(contentSource),
                listContents = ListContentsByTypeUseCase(contentSource),
                getListableContentTypes = GetListableContentTypes(contentSource),
                contentPresenter = MustacheContentPresenter(),
            )

        return Server(
            contentController = contentController,
            config = config,
        )
    }

    companion object {
        fun create() = TestApplication()
    }
}
