package io.github.cekrem.application.usecase

import io.github.cekrem.application.gateway.ContentSource
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class GetContentUseCaseTest {
    private lateinit var contentSource: ContentSource
    private lateinit var getContent: GetContentUseCase

    @BeforeEach
    fun setUp() {
        contentSource = mockk()
        getContent = GetContentUseCase(contentSource)
    }

    @Test
    fun `should return content when it exists`() =
        runTest {
            // Given
            val path = "test/path"
            val expectedContent =
                Content(
                    path = path,
                    title = "Test Content",
                    blocks = emptyList(),
                    type = ContentType("test", listable = false),
                    metadata = Metadata(),
                )
            coEvery { contentSource.getByPath(path) } returns expectedContent

            // When
            val result = getContent(path)

            // Then
            assertEquals(expectedContent, result)
            coVerify(exactly = 1) { contentSource.getByPath(path) }
        }

    @Test
    fun `should return null when content does not exist`() =
        runTest {
            // Given
            val path = "nonexistent/path"
            coEvery { contentSource.getByPath(path) } returns null

            // When
            val result = getContent(path)

            // Then
            assertNull(result)
            coVerify(exactly = 1) { contentSource.getByPath(path) }
        }
}
