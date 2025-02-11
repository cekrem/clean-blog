package io.github.cekrem.domain.model

import kotlinx.datetime.LocalDateTime

data class Metadata(
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val draft: Boolean = false,
    val properties: Map<String, Any> = emptyMap(),
    val publishedAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
