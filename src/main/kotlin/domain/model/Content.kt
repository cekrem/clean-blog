package io.github.cekrem.domain.model

import java.time.LocalDateTime

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
