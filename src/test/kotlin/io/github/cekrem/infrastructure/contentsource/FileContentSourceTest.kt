package io.github.cekrem.infrastructure.contentsource

import io.github.cekrem.application.parser.ContentParser
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import kotlinx.datetime.LocalDateTime
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.writeText
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FileContentSourceTest {
    private val extension = "testExtension"

    @TempDir
    lateinit var tempDir: Path

    private lateinit var contentParser: ContentParser
    private lateinit var fileContentSource: FileContentSource

    @BeforeEach
    fun setUp() {
        contentParser = mockk()
        fileContentSource =
            FileContentSource(
                contentRoot = tempDir.toString(),
                parser = contentParser,
                extension = extension,
            )
    }

    @AfterEach
    fun tearDown() {
        clearMocks(contentParser)
        this.tempDir.toFile().deleteRecursively()
    }

    private fun createTestFile(
        relativePath: String,
        content: String,
        asIndex: Boolean = false,
    ) {
        val fullPath =
            if (asIndex) {
                tempDir.resolve(relativePath).resolve("index.$extension")
            } else {
                tempDir.resolve("$relativePath.$extension")
            }
        fullPath.parent.createDirectories()
        fullPath.writeText(content)
    }

    @Test
    fun `should return null when neither file nor index exists`() {
        val result = fileContentSource.getByPath("non/existent/path")
        assertNull(result)
    }

    @Test
    fun `should parse direct md file`() {
        // Given
        val path = "posts/test-post"
        val content =
            """
            ---
            title: Test Post
            ---
            # Test Content
            """.trimIndent()

        createTestFile(relativePath = path, content = content)

        val expectedContent =
            Content(
                path = path,
                title = "Test Post",
                blocks = emptyList(),
                type = ContentType("posts", listable = true),
                metadata = mockk(),
            )

        every { contentParser.parse(content, path, any()) } returns expectedContent

        // When
        val result = fileContentSource.getByPath(path)

        // Then
        assertEquals(expectedContent, result)
    }

    @Test
    fun `should parse index md file`() {
        // Given
        val path = "posts/test-post"
        val content =
            """
            ---
            title: Test Post with Assets
            ---
            # Test Content
            ![Image](image.jpg)
            """.trimIndent()

        createTestFile(relativePath = path, content = content, asIndex = true)

        val expectedContent =
            Content(
                path = path,
                title = "Test Post with Assets",
                blocks = emptyList(),
                type = ContentType("posts", listable = true),
                metadata = mockk(),
            )

        every { contentParser.parse(content, path, any()) } returns expectedContent

        // When
        val result = fileContentSource.getByPath(path)

        // Then
        assertEquals(expectedContent, result)
    }

    @Test
    fun `should prefer index md over direct md when both exist`() {
        // Given
        val path = "posts/test-post"
        val directContent = "# Direct Content"
        val indexContent = "# Index Content"

        createTestFile(relativePath = path, content = directContent)
        createTestFile(relativePath = path, content = indexContent, asIndex = true)

        val expectedContent =
            Content(
                path = path,
                title = "Test Post",
                blocks = emptyList(),
                type = ContentType("posts", listable = true),
                metadata = Metadata(),
            )

        every { contentParser.parse(indexContent, path, any()) } returns expectedContent

        // When
        val result = fileContentSource.getByPath(path)

        // Then
        assertEquals(expectedContent, result)
    }

    @Test
    fun `should get summaries for content type sorted by publishedAt descending`() {
        // Given
        val type = ContentType("posts", listable = true)
        createTestFile(
            relativePath = "posts/post1",
            content =
                """
                ---
                title: First Post
                publishedAt: 2024-01-01
                ---
                Content
                """.trimIndent(),
            asIndex = false,
        )

        createTestFile(
            relativePath = "posts/post2",
            content =
                """
                ---
                title: Second Post
                publishedAt: 2024-01-02
                ---
                Content
                """.trimIndent(),
            asIndex = true,
        )

        val post1Content =
            Content(
                path = "posts/post1",
                title = "First Post",
                blocks = emptyList(),
                type = type,
                metadata = Metadata(publishedAt = LocalDateTime.parse("2024-01-01T00:00:00.000")),
            )

        val post2Content =
            Content(
                path = "posts/post2",
                title = "Second Post",
                blocks = emptyList(),
                type = type,
                metadata = Metadata(publishedAt = LocalDateTime.parse("2024-01-02T00:00:00.000")),
            )

        every { contentParser.parse(any(), "posts/post1", type) } returns post1Content
        every { contentParser.parse(any(), "posts/post2", type) } returns post2Content

        // When
        val summaries = fileContentSource.getSummariesByType(type)

        // Then
        assertEquals(2, summaries.size)
        // Expect newest first
        assertEquals("Second Post", summaries[0].title)
        assertEquals("First Post", summaries[1].title)
        assertEquals(LocalDateTime.parse("2024-01-02T00:00:00.000"), summaries[0].publishedAt)
        assertEquals(LocalDateTime.parse("2024-01-01T00:00:00.000"), summaries[1].publishedAt)
    }

    @Test
    fun `should get available content types`() {
        // Given
        tempDir.resolve("posts").createDirectories()
        tempDir.resolve("pages").createDirectories()
        tempDir.resolve("regular-file.txt").writeText("not a directory")

        // When
        val types = fileContentSource.getAvailableTypes()

        // Then
        assertEquals(2, types.size)
        assert(types.any { it.name == "posts" && it.listable })
        assert(types.any { it.name == "pages" && it.listable })
    }
}
