package io.github.cekrem.content

import java.time.LocalDateTime

data class ContentType(
        val name: String, // e.g., "posts", "pages", "events"
        val singular: String = name.removeSuffix("s"), // e.g., "post", "page", "event"
)

data class Content(
        val path: String,
        val title: String,
        val content: String,
        val type: ContentType,
        val metadata: Metadata,
        val publishedAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
        val slug: String = path.split("/").last()
)

data class Metadata(
        val description: String? = null,
        val tags: List<String> = emptyList(),
        val draft: Boolean = false,
        val properties: Map<String, Any> = emptyMap()
)
