package io.github.cekrem.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class ContentTypeTest {
    @Test
    fun `should derive singular form from plural name`() {
        // Given/When
        val type = ContentType("posts", listable = true)

        // Then
        assertEquals("post", type.singular)
    }

    @Test
    fun `should validate name format`() {
        // Given
        val invalidNames =
            listOf(
                "", // Empty
                " posts ", // Extra spaces
                "Post", // Capital letter
                "my-posts", // Hyphen
                "posts_archive", // Underscore
            )

        // When/Then
        invalidNames.forEach { name ->
            assertThrows<IllegalArgumentException>("Name '$name' should be invalid") {
                ContentType(name = name, listable = true)
            }
        }
    }
}
