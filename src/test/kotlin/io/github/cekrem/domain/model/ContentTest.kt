package io.github.cekrem.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ContentTest {
    @Test
    fun `should derive slug from path`() {
        // Given
        val path = "posts/my-first-post"

        // When
        val content =
            Content(
                path = path,
                title = "My First Post",
                blocks = emptyList(),
                type = ContentType("posts", listable = true),
                metadata = Metadata(),
            )

        // Then
        assertEquals("my-first-post", content.slug)
    }

    @Test
    fun `should validate path matches content type`() {
        // Given
        val invalidPath = "articles/my-post"
        val type = ContentType("posts", listable = true)

        // When/Then
        assertThrows<IllegalArgumentException> {
            Content(
                path = invalidPath,
                title = "My Post",
                blocks = emptyList(),
                type = type,
                metadata = Metadata(),
            )
        }
    }

    @Test
    fun `should require non-empty title`() {
        // Given
        val emptyTitle = ""

        // When/Then
        assertThrows<IllegalArgumentException> {
            Content(
                path = "posts/my-post",
                title = emptyTitle,
                blocks = emptyList(),
                type = ContentType("posts", listable = true),
                metadata = Metadata(),
            )
        }
    }

    @Test
    fun `should require valid path format`() {
        // Given
        val invalidPaths =
            listOf(
                "posts/my-post/", // Trailing slash
                "posts//my-post", // Double slash
                "posts/my post", // Space in slug
            )

        // When/Then
        invalidPaths.forEach { path ->
            assertThrows<IllegalArgumentException>("Path '$path' should be invalid") {
                Content(
                    path = path,
                    title = "My Post",
                    blocks = emptyList(),
                    type = ContentType("posts", listable = true),
                    metadata = Metadata(),
                )
            }
        }
    }
}
