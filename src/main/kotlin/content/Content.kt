package io.github.cekrem.content

import java.time.LocalDateTime

data class ContentType(
    val name: String, // e.g., "posts", "pages", "events"
    val singular: String = name.removeSuffix("s"), // e.g., "post", "page", "event"
    val listable: Boolean = false,
)

data class Content(
    val path: String,
    val title: String,
    val blocks: List<ContentBlock>,
    val type: ContentType,
    val metadata: Metadata,
    val publishedAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val slug: String = path.split("/").last(),
)

data class ContentSummary(
    val path: String,
    val title: String,
    val type: ContentType,
    val publishedAt: LocalDateTime? = null,
)

data class Metadata(
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val draft: Boolean = false,
    val properties: Map<String, Any> = emptyMap(),
)

sealed interface ContentBlock {
    data class Heading(
        val text: String,
        val level: Int = 1,
    ) : ContentBlock

    data class Text(
        val content: String,
    ) : ContentBlock

    data class Code(
        val content: String,
        val language: String? = null,
    ) : ContentBlock

    data class Quote(
        val content: String,
        val attribution: String? = null,
    ) : ContentBlock

    data class Link(
        val text: String,
        val url: String,
        val external: Boolean = false,
    ) : ContentBlock

    data class Image(
        val url: String,
        val alt: String? = null,
        val caption: String? = null,
    ) : ContentBlock
}
