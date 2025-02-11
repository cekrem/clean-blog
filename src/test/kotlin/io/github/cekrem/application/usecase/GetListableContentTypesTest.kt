package io.github.cekrem.application.usecase

import io.github.cekrem.application.gateway.ContentSource
import io.github.cekrem.domain.model.ContentType
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetListableContentTypesTest {
    private lateinit var contentSource: ContentSource
    private lateinit var getListableTypes: GetListableContentTypes

    @BeforeEach
    fun setUp() {
        contentSource = mockk()
        getListableTypes = GetListableContentTypes(contentSource)
    }

    @Test
    fun `should return only listable content types`() =
        runTest {
            // Given
            val allTypes =
                setOf(
                    ContentType("posts", listable = true),
                    ContentType("pages", listable = false),
                    ContentType("articles", listable = true),
                )
            every { contentSource.getAvailableTypes() } returns allTypes

            // When
            val result = getListableTypes(Unit)

            // Then
            val expected =
                setOf(
                    ContentType("posts", listable = true),
                    ContentType("articles", listable = true),
                )
            assertEquals(expected, result)
            verify(exactly = 1) { contentSource.getAvailableTypes() }
        }

    @Test
    fun `should return empty set when no listable types exist`() =
        runTest {
            // Given
            val allTypes =
                setOf(
                    ContentType("pages", listable = false),
                    ContentType("system", listable = false),
                )
            every { contentSource.getAvailableTypes() } returns allTypes

            // When
            val result = getListableTypes(Unit)

            // Then
            assertEquals(emptySet(), result)
            verify(exactly = 1) { contentSource.getAvailableTypes() }
        }
}
