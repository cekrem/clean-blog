package io.github.cekrem.adapter.dto

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentBlock
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
import io.github.cekrem.domain.model.RichText
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ContentDtosTest {
    @Test
    fun `should convert content to template data`() {
        // Given
        val content =
            Content(
                path = "posts/test",
                title = "Test Post",
                blocks =
                    listOf(
                        ContentBlock.Heading(text = "Hello", level = 1),
                        ContentBlock.Text(segments = listOf(RichText.Plain("World"))),
                    ),
                type = ContentType("posts", listable = true),
                metadata = Metadata(description = "Test description"),
            )

        // When
        val result = content.dto()

        // Then
        assertEquals("Test Post", result.title)
        assertEquals("Test description", result.description)
        assertEquals(2, result.blocks.size)

        with(result.blocks[0]) {
            assertTrue(isHeading)
            assertEquals("Hello", text)
            assertEquals(1, level)
        }

        with(result.blocks[1]) {
            assertTrue(isText)
            assertEquals(1, segments?.size)
            segments?.get(0)?.let {
                assertTrue(it.isPlain)
                assertEquals("World", it.text)
            }
        }
    }

    @Test
    fun `should convert content list to template data`() {
        // Given
        val type = ContentType("posts", listable = true)
        val now = LocalDateTime.parse("2024-01-01T12:00:00")
        val summaries =
            listOf(
                ContentSummary(
                    path = "posts/test1",
                    title = "First Post",
                    type = type,
                    publishedAt = now,
                ),
                ContentSummary(
                    path = "posts/test2",
                    title = "Second Post",
                    type = type,
                ),
            )

        // When
        val result = summaries.dto()

        // Then
        assertEquals(2, result.size)

        val first = result.first() as Map<*, *>
        assertEquals("First Post", first["title"])
        assertEquals("posts/test1", first["path"])
        assertEquals(now.toString(), first["publishedAt"])
    }
}
