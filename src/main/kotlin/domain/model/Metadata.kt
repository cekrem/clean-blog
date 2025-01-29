package io.github.cekrem.domain.model

data class Metadata(
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val draft: Boolean = false,
    val properties: Map<String, Any> = emptyMap(),
)