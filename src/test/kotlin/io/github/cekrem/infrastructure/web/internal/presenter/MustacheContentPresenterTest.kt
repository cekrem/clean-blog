package io.github.cekrem.infrastructure.web.internal.presenter

import io.github.cekrem.adapter.dto.ContentBlockDto
import io.github.cekrem.adapter.dto.ContentDto
import io.github.cekrem.adapter.dto.ContentSummaryDto
import io.github.cekrem.adapter.dto.RichTextDto
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

        with(data.blocks[0] as ContentBlockDto.Heading) {
            assertTrue(blockTypes["heading"] == true)
            assertEquals("Introduction", properties["text"])
            assertEquals(2, properties["level"])
        }

        with(data.blocks[1] as ContentBlockDto.Text) {
            assertTrue(blockTypes["text"] == true)
            val segments = properties["segments"] as List<*>
            assertEquals(2, segments.size)

            with(segments[0] as RichTextDto.Plain) {
                assertTrue(textTypes["plain"] == true)
                assertEquals("Hello ", properties["text"])
            }
        }
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

        with(data.blocks[0] as ContentBlockDto.Heading) {
            assertTrue(blockTypes["heading"] == true)
            assertEquals("Title", properties["text"])
            assertEquals(1, properties["level"])
        }
        with(data.blocks[1] as ContentBlockDto.Text) {
            assertTrue(blockTypes["text"] == true)
            val segments = properties["segments"] as List<*>
            assertEquals(1, segments.size)

            with(segments[0] as RichTextDto.Plain) {
                assertTrue(textTypes["plain"] == true)
                assertEquals("Plain text", properties["text"])
            }
        }
        with(data.blocks[2] as ContentBlockDto.Code) {
            assertTrue(blockTypes["code"] == true)
            assertEquals("println(\"Hello\")", properties["content"])
            assertEquals("kotlin", properties["language"])
        }
        with(data.blocks[3] as ContentBlockDto.Quote) {
            assertTrue(blockTypes["quote"] == true)
            assertEquals("Famous quote", properties["content"])
            assertEquals("Author", properties["attribution"])
        }
        with(data.blocks[4] as ContentBlockDto.Link) {
            assertTrue(blockTypes["link"] == true)
            assertEquals("Click me", properties["text"])
            assertEquals("https://example.com", properties["url"])
            assertEquals(true, properties["external"])
        }
        with(data.blocks[5] as ContentBlockDto.Image) {
            assertTrue(blockTypes["image"] == true)
            assertEquals("https://example.com/image.jpg", properties["url"])
            assertEquals("Test image", properties["alt"])
        }
    }

    @Test
    fun `should properly render plain text content`() {
        // Given
        val content =
            Content(
                path = "posts/test-post",
                title = "Test Post",
                blocks =
                    listOf(
                        ContentBlock.Text(
                            segments =
                                listOf(
                                    RichText.Plain("This is a simple paragraph of text."),
                                ),
                        ),
                    ),
                type = ContentType("posts", listable = true),
                metadata = Metadata(),
            )

        // When
        val result = presenter.presentContent(content)

        // Then
        assertTrue(result is MustacheContent)
        val data = result.model as ContentDto
        assertEquals(1, data.blocks.size)

        with(data.blocks[0] as ContentBlockDto.Text) {
            assertTrue(blockTypes["text"] == true)
            val segments = properties["segments"] as List<*>
            assertEquals(1, segments.size)

            with(segments[0] as RichTextDto.Plain) {
                assertTrue(textTypes["plain"] == true)
                assertEquals("This is a simple paragraph of text.", properties["text"])
            }
        }
    }

    @Test
    fun `should render mixed content with plain text and links`() {
        // Given
        val content =
            Content(
                path = "posts/test-post",
                title = "Test Post",
                blocks =
                    listOf(
                        ContentBlock.Text(
                            segments =
                                listOf(
                                    RichText.Plain("Here is some text with a "),
                                    RichText.InlineLink(
                                        text = "link",
                                        url = "https://example.com",
                                        external = true,
                                    ),
                                    RichText.Plain(" in the middle."),
                                ),
                        ),
                    ),
                type = ContentType("posts", listable = true),
                metadata = Metadata(),
            )

        // When
        val result = presenter.presentContent(content)

        // Then
        assertTrue(result is MustacheContent)
        val data = result.model as ContentDto
        assertEquals(1, data.blocks.size)

        with(data.blocks[0] as ContentBlockDto.Text) {
            assertTrue(blockTypes["text"] == true)
            val segments = properties["segments"] as List<*>
            assertEquals(3, segments.size)

            with(segments[0] as RichTextDto.Plain) {
                assertTrue(textTypes["plain"] == true)
                assertEquals("Here is some text with a ", properties["text"])
            }

            with(segments[1] as RichTextDto.InlineLink) {
                assertTrue(textTypes["inlineLink"] == true)
                assertEquals("link", properties["text"])
                assertEquals("https://example.com", properties["url"])
            }

            with(segments[2] as RichTextDto.Plain) {
                assertTrue(textTypes["plain"] == true)
                assertEquals(" in the middle.", properties["text"])
            }
        }
    }
}
