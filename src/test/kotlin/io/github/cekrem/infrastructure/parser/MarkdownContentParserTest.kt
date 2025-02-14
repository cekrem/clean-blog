package io.github.cekrem.infrastructure.parser

import io.github.cekrem.application.parser.ContentParser
import io.github.cekrem.domain.model.ContentBlock
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
import io.github.cekrem.domain.model.RichText
import kotlinx.coroutines.test.runTest
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class MarkdownContentParserTest {
    private lateinit var contentParser: ContentParser

    @BeforeEach
    fun setUp() {
        contentParser = MarkdownContentParser()
    }

    @Test
    fun `should parse yaml front matter`() {
        runTest {
            // Given
            val path = "/path/to/file.md"
            val contentType = ContentType(name = "posts", listable = true)
            val rawContent =
                """
                ---
                title: Test Post
                description: This is a test post.
                tags: ["foo", "bar", "baz"]
                publishedAt: 2024-01-01T12:00:00
                updatedAt: 2025-01-01T12:00:00
                draft: true
                ---
                # Some content that we ignore in this test
                """.trimIndent()
            val expectedTitle = "Test Post"
            val expectedMetadata =
                Metadata(
                    description = "This is a test post.",
                    publishedAt = LocalDateTime.parse("2024-01-01T12:00:00"),
                    updatedAt = LocalDateTime.parse("2025-01-01T12:00:00"),
                    tags = listOf("foo", "bar", "baz"),
                    draft = true,
                )

            // When
            val result =
                contentParser.parse(
                    rawContent = rawContent,
                    path = path,
                    type = contentType,
                )

            // Then
            assertEquals(expectedTitle, result.title)
            assertEquals(expectedMetadata, result.metadata)
        }
    }

    @Test
    fun `should parse heading content blocks correctly`() {
        runTest {
            // Given
            val path = "/path/to/file.md"
            val contentType = ContentType(name = "posts", listable = true)
            val rawContent =
                """
                ---
                title: Test Post
                ---
                # Level 1 heading
                ## Level 2 heading
                ### Level 3 heading
                #### Level 4 heading
                ##### Level 5 heading
                ###### Level 6 heading
                """.trimIndent()

            // When
            val result =
                contentParser.parse(
                    rawContent = rawContent,
                    path = path,
                    type = contentType,
                )

            // Then
            assertEquals(result.blocks[0], ContentBlock.Heading(level = 1, text = "Level 1 heading"))
            assertEquals(result.blocks[1], ContentBlock.Heading(level = 2, text = "Level 2 heading"))
            assertEquals(result.blocks[2], ContentBlock.Heading(level = 3, text = "Level 3 heading"))
            assertEquals(result.blocks[3], ContentBlock.Heading(level = 4, text = "Level 4 heading"))
            assertEquals(result.blocks[4], ContentBlock.Heading(level = 5, text = "Level 5 heading"))
            assertEquals(result.blocks[5], ContentBlock.Heading(level = 6, text = "Level 6 heading"))
        }
    }

    @Test
    fun `should parse text content blocks correctly`() {
        runTest {
            // Given
            val path = "/path/to/file.md"
            val contentType = ContentType(name = "posts", listable = true)
            val rawContent =
                """
                ---
                title: Test Post
                ---
                This is a paragraph of text.
                
                This is another paragraph with *italic* and **bold** text.
                """.trimIndent()

            // When
            val result =
                contentParser.parse(
                    rawContent = rawContent,
                    path = path,
                    type = contentType,
                )

            // Then
            assertEquals(
                ContentBlock.Text(listOf(RichText.Plain("This is a paragraph of text."))),
                result.blocks[0],
            )
            assertEquals(
                ContentBlock.Text(listOf(RichText.Plain("This is another paragraph with *italic* and **bold** text."))),
                result.blocks[1],
            )
        }
    }

    @Test
    fun `should parse code blocks correctly`() {
        runTest {
            // Given
            val path = "/path/to/file.md"
            val contentType = ContentType(name = "posts", listable = true)
            val rawContent =
                """
                ---
                title: Test Post
                ---
                ```kotlin
                fun hello() {
                    println("Hello, World!")
                }
                ```
                
                ```
                Plain code block
                ```
                """.trimIndent()

            // When
            val result =
                contentParser.parse(
                    rawContent = rawContent,
                    path = path,
                    type = contentType,
                )

            // Then
            assertEquals(
                ContentBlock.Code(
                    content =
                        """
                        fun hello() {
                            println("Hello, World!")
                        }
                        """.trimIndent(),
                    language = "kotlin",
                ),
                result.blocks[0],
            )
            assertEquals(
                ContentBlock.Code(
                    content = "Plain code block",
                    language = null,
                ),
                result.blocks[1],
            )
        }
    }

    @Test
    fun `should parse quote blocks correctly`() {
        runTest {
            // Given
            val path = "/path/to/file.md"
            val contentType = ContentType(name = "posts", listable = true)
            val rawContent =
                """
                ---
                title: Test Post
                ---
                > This is a quote
                > with multiple lines
                > -- Author Name
                
                > Simple quote without attribution
                """.trimIndent()

            // When
            val result =
                contentParser.parse(
                    rawContent = rawContent,
                    path = path,
                    type = contentType,
                )

            // Then
            assertEquals(
                ContentBlock.Quote(
                    content = "This is a quote with multiple lines",
                    attribution = "Author Name",
                ),
                result.blocks[0],
            )
            assertEquals(
                ContentBlock.Quote(
                    content = "Simple quote without attribution",
                    attribution = null,
                ),
                result.blocks[1],
            )
        }
    }

    @Test
    fun `should parse link blocks correctly`() {
        runTest {
            // Given
            val path = "/path/to/file.md"
            val contentType = ContentType(name = "posts", listable = true)
            val rawContent =
                """
                ---
                title: Test Post
                ---
                [Internal Link](/internal/path)
                
                [External Link](https://example.com)
                """.trimIndent()

            // When
            val result =
                contentParser.parse(
                    rawContent = rawContent,
                    path = path,
                    type = contentType,
                )

            // Then
            assertEquals(
                ContentBlock.Link(
                    text = "Internal Link",
                    url = "/internal/path",
                    external = false,
                ),
                result.blocks[0],
            )
            assertEquals(
                ContentBlock.Link(
                    text = "External Link",
                    url = "https://example.com",
                    external = true,
                ),
                result.blocks[1],
            )
        }
    }

    @Test
    fun `should parse image blocks correctly`() {
        runTest {
            // Given
            val path = "/path/to/file.md"
            val contentType = ContentType(name = "posts", listable = true)
            val rawContent =
                """
                ---
                title: Test Post
                ---
                ![Alt text](/path/to/image.jpg "Image caption")
                
                ![Simple image](/path/to/simple.jpg)
                """.trimIndent()

            // When
            val result =
                contentParser.parse(
                    rawContent = rawContent,
                    path = path,
                    type = contentType,
                )

            // Then
            assertEquals(
                ContentBlock.Image(
                    url = "/path/to/image.jpg",
                    alt = "Alt text",
                    caption = "Image caption",
                ),
                result.blocks[0],
            )
            assertEquals(
                ContentBlock.Image(
                    url = "/path/to/simple.jpg",
                    alt = "Simple image",
                    caption = null,
                ),
                result.blocks[1],
            )
        }
    }

    @Test
    fun `should parse text blocks with inline links correctly`() {
        runTest {
            // Given
            val path = "/path/to/file.md"
            val contentType = ContentType(name = "posts", listable = true)
            val rawContent =
                """
                ---
                title: Test Post
                ---
                This is a paragraph with an [inline link](https://example.com) and some more text.
                """.trimIndent()

            // When
            val result =
                contentParser.parse(
                    rawContent = rawContent,
                    path = path,
                    type = contentType,
                )

            // Then
            assertEquals(
                ContentBlock.Text(
                    segments =
                        listOf(
                            RichText.Plain("This is a paragraph with an "),
                            RichText.InlineLink(
                                text = "inline link",
                                url = "https://example.com",
                                external = true,
                            ),
                            RichText.Plain(" and some more text."),
                        ),
                ),
                result.blocks[0],
            )
        }
    }
}
