package io.github.cekrem.infrastructure.web.internal.presenter

import io.github.cekrem.adapter.dto.ContentDto
import io.github.cekrem.adapter.dto.ContentSummaryDto
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentBlock
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
import io.github.cekrem.domain.model.RichText
import io.ktor.server.mustache.MustacheContent
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class MustacheContentPresenterTest {
    private lateinit var presenter: MustacheContentPresenter

    @BeforeEach
    fun setUp() {
        presenter = MustacheContentPresenter()
    }

    @Test
    fun `should render content page with correct template and data`() {
        // Given
        val content =
            Content(
                path = "posts/test-post",
                title = "Test Post",
                blocks =
                    listOf(
                        ContentBlock.Heading(text = "Introduction", level = 2),
                        ContentBlock.Text(
                            segments =
                                listOf(
                                    RichText.Plain("Hello "),
                                    RichText.InlineLink(
                                        text = "world",
                                        url = "https://example.com",
                                        external = true,
                                    ),
                                ),
                        ),
                    ),
                type = ContentType("posts", listable = true),
                metadata = Metadata(description = "Test description"),
            )

        // When
        val result = presenter.presentContent(content)

        // Then
        assertEquals("content.mustache", result.template)

        val data = result.model as ContentDto
        assertEquals("Test Post", data.title)
        assertEquals("Test description", data.description)
        assertEquals(2, data.blocks.size)
    }

    @Test
    fun `should render content list with correct template and data`() {
        // Given
        val type = ContentType("posts", listable = true)
        val summaries =
            listOf(
                ContentSummary(
                    path = "posts/post1",
                    title = "First Post",
                    type = type,
                ),
                ContentSummary(
                    path = "posts/post2",
                    title = "Second Post",
                    type = type,
                ),
            )

        // When
        val result = presenter.presentContentList(summaries)

        // Then
        assertTrue(result is MustacheContent)
        assertEquals("list.mustache", result.template)

        val data = result.model as List<ContentSummaryDto>
        assertEquals(2, data.size)
        assertEquals("First Post", data[0]["title"])
        assertEquals("Second Post", data[1]["title"])
    }

    @Test
    fun `should handle content with all block types`() {
        // Given
        val content =
            Content(
                path = "posts/full-test",
                title = "Full Test",
                blocks =
                    listOf(
                        ContentBlock.Heading(text = "Title", level = 1),
                        ContentBlock.Text(segments = listOf(RichText.Plain("Plain text"))),
                        ContentBlock.Code(content = "println(\"Hello\")", language = "kotlin"),
                        ContentBlock.Quote(content = "Famous quote", attribution = "Author"),
                        ContentBlock.Link(text = "Click me", url = "https://example.com"),
                        ContentBlock.Image(url = "https://example.com/image.jpg", alt = "Test image"),
                    ),
                type = ContentType("posts", listable = true),
                metadata = Metadata(),
            )

        // When
        val result = presenter.presentContent(content)

        // Then
        assertTrue(result is MustacheContent)
        val data = result.model as ContentDto
        assertEquals(6, data.blocks.size)
    }
}
