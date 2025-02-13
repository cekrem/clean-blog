package io.github.cekrem.infrastructure.parser

import io.github.cekrem.application.parser.ContentParser
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
                tags = ["foo", "bar", "baz"]
                publishedAt: 2024-01-01T12:00:00
                updatedAt: 2025-01-01T12:00:00
                draft: true
                ---
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
            val result = contentParser.parse(path, rawContent, contentType)

            // Then
            assertEquals(expectedTitle, result.title)
            assertEquals(expectedMetadata, result.metadata)
        }
    }

    @Test
    fun `should parse heading content blocks correctly`() {
        runTest {
            // TODO
        }
    }
}
