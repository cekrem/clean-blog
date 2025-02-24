package io.github.cekrem.adapter.controller

import io.github.cekrem.adapter.dto.dto
import io.github.cekrem.adapter.presenter.ContentPresenter
import io.github.cekrem.application.usecase.GetContentUseCase
import io.github.cekrem.application.usecase.GetListableContentTypes
import io.github.cekrem.application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ContentControllerTest {
    private lateinit var getContent: GetContentUseCase
    private lateinit var listContents: ListContentsByTypeUseCase
    private lateinit var getListableContentTypes: GetListableContentTypes
    private lateinit var contentPresenter: ContentPresenter
    private lateinit var controller: ContentController

    @BeforeEach
    fun setUp() {
        getContent = mockk()
        listContents = mockk()
        getListableContentTypes = mockk()
        contentPresenter = mockk()
        controller =
            ContentController(
                getContent = getContent,
                listContents = listContents,
                getListableContentTypes = getListableContentTypes,
                contentPresenter = contentPresenter,
            )
    }

    @Test
    fun `should return 404 when type or slug is null`() =
        runTest {
            // When/Then
            assertEquals(404, controller.getContentResponse(type = null, slug = "test").statusCode)
            assertEquals(404, controller.getContentResponse(type = "test", slug = null).statusCode)
            assertEquals(404, controller.getContentResponse(type = null, slug = null).statusCode)
        }

    @Test
    fun `should return 404 when content does not exist`() =
        runTest {
            // Given
            coEvery { getContent("posts/test") } returns null

            // When
            val response = controller.getContentResponse(type = "posts", slug = "test")

            // Then
            assertEquals(404, response.statusCode)
            assertNull(response.body)
            coVerify(exactly = 1) { getContent("posts/test") }
        }

    @Test
    fun `should return 200 with content when content exists`() =
        runTest {
            // Given
            val content =
                Content(
                    path = "posts/test",
                    title = "Test Post",
                    blocks = emptyList(),
                    type = ContentType("posts", listable = true),
                    metadata = Metadata(),
                )
            val presentedContent = mockk<Any>()

            coEvery { getContent("posts/test") } returns content
            every { contentPresenter.presentContent(content.dto()) } returns presentedContent

            // When
            val response = controller.getContentResponse(type = "posts", slug = "test")

            // Then
            assertEquals(200, response.statusCode)
            assertEquals(presentedContent, response.body)
            coVerify(exactly = 1) {
                getContent("posts/test")
                contentPresenter.presentContent(content.dto())
            }
        }

    @Test
    fun `should return content list for valid content type`() =
        runTest {
            // Given
            val type = ContentType("posts", listable = true)
            val summaries =
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
            val presentedList = mockk<Any>()

            coEvery { listContents(type) } returns summaries
            every { contentPresenter.presentContentList(summaries.dto()) } returns presentedList

            // When
            val response = controller.getListContentsResponse(type)

            // Then
            assertEquals(200, response.statusCode)
            assertEquals(presentedList, response.body)
            coVerify(exactly = 1) {
                listContents(type)
                contentPresenter.presentContentList(summaries.dto())
            }
        }

    @Test
    fun `should return health check response`() =
        runTest {
            // When
            val response = controller.healthCheckResponse()

            // Then
            assertEquals(200, response.statusCode)
            assertEquals("OK", response.body)
        }

    @Test
    fun `should return listable content types`() =
        runTest {
            // Given
            val types =
                setOf(
                    ContentType("posts", listable = true),
                    ContentType("pages", listable = true),
                )
            coEvery { getListableContentTypes(Unit) } returns types

            // When
            val result = controller.getListableContentTypes()

            // Then
            assertEquals(types, result)
            coVerify(exactly = 1) { getListableContentTypes(Unit) }
        }

    @Test
    fun `should handle index page request`() =
        runTest {
            // Given
            val indexContent =
                Content(
                    path = "pages/index",
                    title = "Index",
                    blocks = emptyList(),
                    type = ContentType("pages", listable = false),
                    metadata = Metadata(),
                )
            val presentedContent = mockk<Any>()

            coEvery { getContent("pages/index") } returns indexContent
            every { contentPresenter.presentContent(indexContent.dto()) } returns presentedContent

            // When
            val response = controller.getIndexResponse()

            // Then
            assertEquals(200, response.statusCode)
            assertEquals(presentedContent, response.body)
            coVerify(exactly = 1) {
                getContent("pages/index")
                contentPresenter.presentContent(indexContent.dto())
            }
        }
}
