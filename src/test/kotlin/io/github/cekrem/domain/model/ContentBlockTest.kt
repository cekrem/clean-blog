package io.github.cekrem.domain.model

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class ContentBlockTest {
    @Test
    fun `should validate heading level`() {
        // Given
        val invalidLevels = listOf(0, 7)

        // When/Then
        invalidLevels.forEach { level ->
            assertThrows<IllegalArgumentException> {
                ContentBlock.Heading(
                    level = level,
                    text = "Test",
                )
            }
        }
    }

    @Test
    fun `should validate heading text is not empty`() {
        // Given
        val invalidTexts = listOf("", " ", "\n")

        // When/Then
        invalidTexts.forEach { text ->
            assertThrows<IllegalArgumentException> {
                ContentBlock.Heading(
                    level = 1,
                    text = text,
                )
            }
        }
    }

    @Test
    fun `should validate text segments are not empty`() {
        assertThrows<IllegalArgumentException> {
            ContentBlock.Text(segments = emptyList())
        }
    }

    @Test
    fun `should validate code block language`() {
        // Given
        val invalidLanguages =
            listOf(
                "C++", // No special characters allowed
                "UPPERCASE",
                "with space",
                "", // Empty not allowed
            )

        // When/Then
        invalidLanguages.forEach { language ->
            assertThrows<IllegalArgumentException> {
                ContentBlock.Code(
                    language = language,
                    content = "test",
                )
            }
        }
    }

    @Test
    fun `should validate quote content is not empty`() {
        // Given
        val invalidContents = listOf("", " ", "\n")

        // When/Then
        invalidContents.forEach { content ->
            assertThrows<IllegalArgumentException> {
                ContentBlock.Quote(
                    content = content,
                    attribution = "Someone",
                )
            }
        }
    }

    @Test
    fun `should validate link text and url`() {
        // Given
        val invalidUrls =
            listOf(
                "not-a-url",
                "ftp://invalid-scheme.com",
                "https://toolong${"a".repeat(500)}.com",
            )

        // When/Then
        // Test empty text
        assertThrows<IllegalArgumentException> {
            ContentBlock.Link(
                text = "",
                url = "https://valid-url.com/image.jpg",
            )
        }

        // Test invalid URLs
        invalidUrls.forEach { url ->
            assertThrows<IllegalArgumentException> {
                ContentBlock.Link(
                    text = "Valid text",
                    url = url,
                )
            }
        }
    }

    @Test
    fun `should validate image url`() {
        // Given
        val invalidUrls =
            listOf(
                "not-a-url",
                "ftp://invalid-scheme.com",
                "https://toolong${"a".repeat(500)}.com",
            )

        // When/Then
        invalidUrls.forEach { url ->
            println("about to validate url: $url")
            assertThrows<IllegalArgumentException> {
                ContentBlock.Image(
                    url = url,
                    alt = null, // alt is optional
                    caption = "Optional caption",
                )
            }
        }
    }

    @Test
    fun `should validate code block content`() {
        // Given
        val invalidContents = listOf("", " ", "\n")

        // When/Then
        invalidContents.forEach { content ->
            assertThrows<IllegalArgumentException> {
                ContentBlock.Code(
                    content = content,
                    language = null, // language is optional
                )
            }
        }
    }

    @Test
    fun `should allow valid content blocks`() {
        // These should not throw exceptions
        ContentBlock.Heading(level = 1, text = "Valid heading")
        ContentBlock.Quote(content = "Valid quote", attribution = "Someone")
        ContentBlock.Link(text = "Valid link", url = "https://example.com/image.jpg")
        ContentBlock.Image(
            url = "https://example.com/image.jpg",
            alt = null,
            caption = "Valid caption",
        )
    }
}
