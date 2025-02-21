package io.github.cekrem.adapter.dto

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary

typealias ContentSummaryDto = Map<String, String>

data class ContentDto(
    val title: String,
    val description: String,
    val blocks: List<ContentBlockDto>,
)

fun Content.dto(): ContentDto =
    ContentDto(
        title = this.title,
        description = this.metadata.description ?: "",
        blocks = this.blocks.map { it.toDto() },
    )

fun List<ContentSummary>.dto(): List<ContentSummaryDto> =
    map { summary ->
        mapOf(
            "title" to summary.title,
            "path" to summary.path,
            "publishedAt" to summary.publishedAt.toString(),
        )
    }
