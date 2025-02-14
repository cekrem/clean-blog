package io.github.cekrem.infrastructure.parser

import io.github.cekrem.application.parser.ContentParser
import io.github.cekrem.domain.model.ContentBlock
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
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
}
