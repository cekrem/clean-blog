package io.github.cekrem.domain.model

import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class MetadataTest {
    @Test
    fun `should validate description length`() {
        // Given
        val tooLongDescription = "a".repeat(501)

        // When/Then
        assertThrows<IllegalArgumentException> {
            Metadata(description = tooLongDescription)
        }
    }

    @Test
    fun `should validate tag format`() {
        // Given
        val invalidTags =
            listOf(
                "Tag with trailing end ",
                "UPPERCASE",
                "special-chars!",
                "",
                "a", // too short
                "a".repeat(31), // too long
            )

        // When/Then
        invalidTags.forEach { tag ->
            assertThrows<IllegalArgumentException>("Tag '$tag' should be invalid") {
                Metadata(tags = listOf(tag))
            }
        }
    }

    @Test
    fun `should validate dates`() {
        // Given
        val publishedAt = LocalDateTime.parse("2024-01-01T12:00:00")
        val earlierUpdate = LocalDateTime.parse("2023-01-01T12:00:00")

        // When/Then
        assertThrows<IllegalArgumentException> {
            Metadata(
                publishedAt = publishedAt,
                updatedAt = earlierUpdate,
            )
        }
    }

    @Test
    fun `should determine if content is published`() {
        // Given
        val now = LocalDateTime.parse("2024-01-01T12:00:00")
        val future = LocalDateTime.parse("2025-01-01T12:00:00")
        val past = LocalDateTime.parse("2023-01-01T12:00:00")

        // When/Then
        assertFalse(
            Metadata(
                publishedAt = future,
                draft = false,
            ).isPublished(now),
        )

        assertFalse(
            Metadata(
                publishedAt = past,
                draft = true,
            ).isPublished(now),
        )

        assertTrue(
            Metadata(
                publishedAt = past,
                draft = false,
            ).isPublished(now),
        )
    }
}
