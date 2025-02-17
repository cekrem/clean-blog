package io.github.cekrem.acceptance.features

import TestFixtures
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.withCharset
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class ServeMarkdownBlogPostFeatureTest : FeatureAcceptanceTest() {
    @Test
    fun `should convert and serve markdown blog posts as properly formatted HTML pages`() =
        runTest {
            TestFixtures.blogPosts.forEach { post ->
                // Given a blog post exists
                testApplication.givenBlogPost(
                    slug = post,
                    content = TestFixtures.readMarkdownPost(post),
                )

                // When requesting the blog post
                val response = testClient.get("${testApplication.baseUrl}/posts/$post")

                // Then it should return properly formatted HTML
                assertEquals(HttpStatusCode.OK, response.status)
                assertEquals(
                    ContentType.Text.Html.withCharset(Charsets.UTF_8),
                    response.contentType(),
                )
                assertEquals(TestFixtures.readHtmlFixture(post), response.bodyAsText())
            }
        }

    @Test
    fun `should return 404 when blog post does not exist`() =
        runTest {
            // When requesting a non-existent blog post
            val response = testClient.get("${testApplication.baseUrl}/posts/non-existent")

            // Then it should return 404
            assertEquals(HttpStatusCode.NotFound, response.status)
        }

    @Test
    fun `should return 500 when markdown is malformed`() =
        runTest {
            // Given a blog post with malformed markdown
            testApplication.givenBlogPost(
                slug = "malformed",
                content = "Invalid --- frontmatter",
            )

            // When requesting the blog post
            val response = testClient.get("${testApplication.baseUrl}/posts/malformed")

            // Then it should return 500
            assertEquals(HttpStatusCode.InternalServerError, response.status)
        }
}
