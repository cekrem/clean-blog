package io.github.cekrem.domain.model

import kotlinx.datetime.LocalDateTime

data class ContentSummary(
    val path: String,
    val title: String,
    val type: ContentType,
    val publishedAt: LocalDateTime? = null,
)
