package io.github.cekrem.application.usecase

import io.github.cekrem.application.gateway.ContentSource
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ListContentsByTypeUseCaseTest {
    private lateinit var contentSource: ContentSource
    private lateinit var listContentsByType: ListContentsByTypeUseCase

    @BeforeEach
    fun setUp() {
        contentSource = mockk()
        listContentsByType = ListContentsByTypeUseCase(contentSource)
    }

    @Test
    fun `should return list of content summaries for given type`() =
        runTest {
            // Given
            val type = ContentType("posts", listable = true)
            val expectedSummaries =
                listOf(
                    ContentSummary(
                        path = "posts/test1",
                        title = "Test Post 1",
                        type = type,
                    ),
                    ContentSummary(
                        path = "posts/test2",
                        title = "Test Post 2",
                        type = type,
                    ),
                )
            coEvery { contentSource.getSummariesByType(type) } returns expectedSummaries

            // When
            val result = listContentsByType(type)

            // Then
            assertEquals(expectedSummaries, result)
            coVerify(exactly = 1) { contentSource.getSummariesByType(type) }
        }

    @Test
    fun `should return empty list when no content exists for type`() =
        runTest {
            // Given
            val type = ContentType("empty", listable = true)
            coEvery { contentSource.getSummariesByType(type) } returns emptyList()

            // When
            val result = listContentsByType(type)

            // Then
            assertEquals(emptyList(), result)
            coVerify(exactly = 1) { contentSource.getSummariesByType(type) }
        }
}
