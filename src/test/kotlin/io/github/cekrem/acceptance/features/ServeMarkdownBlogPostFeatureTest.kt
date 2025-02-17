package io.github.cekrem.acceptance.features

import io.github.cekrem.application.usecase.GetContentUseCase
import io.github.cekrem.application.usecase.GetListableContentTypes
import io.github.cekrem.application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.infrastructure.contentsource.FileContentSource
import io.github.cekrem.infrastructure.parser.MarkdownContentParser
import io.github.cekrem.infrastructure.web.Server
import io.github.cekrem.infrastructure.web.ServerConfig
import io.github.cekrem.infrastructure.web.internal.presenter.MustacheContentPresenter
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ServeMarkdownBlogPostFeatureTest {
    private val markdownPost =
        """
        ---
        title: Hello World
        description: My first blog post
        publishedAt: 2024-01-01T12:00:00
        ---
        
        This is a paragraph.
        
        ## Heading2
        
        ```kotlin
        fun hello() = "world"
        ```
        """.trimIndent()

    private val htmlPostBody =
        """
        <p>This is a paragraph.</p>
        <h2>Heading2</h2>
        <pre>
            <code class="language-kotlin">
                fun hello() = "world"
            </code>
        </pre>
        """.trimIndent()

    private val serverConfig = ServerConfig(debug = true)
    private val client = HttpClient(CIO)

    @TempDir
    private lateinit var tempDir: Path

    private lateinit var server: Server

    @BeforeEach
    fun setUp() {
        // Write real markdown files
        tempDir.resolve("posts").createDirectories()
        tempDir.resolve("posts/hello-world.md").writeText(markdownPost)

        initializeServer()
        server.start()
        runBlocking { server.ensureReady() }
    }

    @AfterEach
    fun tearDown() {
        client.close()
        server.stop()

        // Clean up actual files
        this.tempDir.toFile().deleteRecursively()
    }

    @Test
    fun `should serve blog post with proper formatting`() =
        runTest {
            // Make actual HTTP request
            val response = client.get("http://localhost:${serverConfig.port}/posts/hello-world")

            // Verify HTTP response
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(ContentType.Text.Html.withCharset(Charsets.UTF_8), response.contentType())

            // Verify actual HTML content
            val htmlText = response.bodyAsText()
            assertEquals(htmlPostBody, htmlText)
        }

    @Test
    fun `should generate RSS feed from actual content`() =
        runTest {
            // Make actual HTTP request to RSS endpoint
            val response = client.get("http://localhost:${serverConfig.port}/feed.xml")

            // Verify real RSS output
            assertEquals(HttpStatusCode.OK, response.status)
            assertEquals(ContentType.Application.Rss.withCharset(Charsets.UTF_8), response.contentType())

            val rssContent = response.bodyAsText()
            assertTrue(rssContent.contains("<title>Hello World</title>"))
            assertTrue(rssContent.contains("<pubDate>2024-01-01T12:00:00</pubDate>"))
        }

    private fun initializeServer() {
        val contentSource =
            FileContentSource(
                contentRoot = tempDir.toString(),
                parser = MarkdownContentParser(),
                extension = "md",
            )

        server =
            Server(
                getContent = GetContentUseCase(contentSource),
                listContents = ListContentsByTypeUseCase(contentSource),
                getListableContentTypes = GetListableContentTypes(contentSource),
                contentPresenter = MustacheContentPresenter(),
                config = serverConfig,
            )
    }
}
