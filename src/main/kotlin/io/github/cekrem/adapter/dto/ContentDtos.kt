package io.github.cekrem.adapter.dto

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentBlock
import io.github.cekrem.domain.model.ContentSummary

typealias ContentBlockWrapperDto = Map<String, ContentBlock>
typealias ContentSummaryDto = Map<String, String>

data class ContentDto(
    val title: String,
    val description: String,
    val blocks: List<ContentBlockWrapperDto>,
)

fun Content.dto(): ContentDto =
    ContentDto(
        title = this.title,
        description = this.metadata.description ?: "",
        blocks =
            this.blocks.map { block ->
                mapOf(
                    when (block) {
                        is ContentBlock.Heading -> "Heading"
                        is ContentBlock.Text -> "Text"
                        is ContentBlock.Code -> "Code"
                        is ContentBlock.Quote -> "Quote"
                        is ContentBlock.Link -> "Link"
                        is ContentBlock.Image -> "Image"
                    } to block,
                )
            },
    )

fun List<ContentSummary>.dto(): List<ContentSummaryDto> =
    map { summary ->
        mapOf(
            "title" to summary.title,
            "path" to summary.path,
            "publishedAt" to summary.publishedAt.toString(),
        )
    }
