package io.github.cekrem.web.dto

import io.github.cekrem.content.Content
import io.github.cekrem.content.ContentSummary
import io.github.cekrem.content.ContentType
import io.github.cekrem.content.Metadata
import kotlinx.serialization.Serializable

@Serializable
data class ContentTypeDto(
    val name: String,
    val singular: String,
    val listable: Boolean,
) {
    companion object {
        fun from(type: ContentType) =
            ContentTypeDto(
                name = type.name,
                singular = type.singular,
                listable = type.listable,
            )
    }
}

@Serializable
data class ContentDto(
    val path: String,
    val title: String,
    val blocks: List<ContentBlockDto>,
    val type: ContentTypeDto,
    val metadata: MetadataDto,
    val publishedAt: String? = null,
    val updatedAt: String? = null,
    val slug: String,
) {
    companion object {
        fun from(content: Content) =
            ContentDto(
                path = content.path,
                title = content.title,
                blocks = content.blocks.map { ContentBlockDto.from(it) },
                type = ContentTypeDto.from(content.type),
                metadata = MetadataDto.from(content.metadata),
                publishedAt = content.publishedAt?.toString(),
                updatedAt = content.updatedAt?.toString(),
                slug = content.slug,
            )
    }
}

@Serializable
data class ContentSummaryDto(
    val path: String,
    val title: String,
    val type: ContentTypeDto,
    val publishedAt: String? = null,
) {
    companion object {
        fun from(summary: ContentSummary) =
            ContentSummaryDto(
                path = summary.path,
                title = summary.title,
                type = ContentTypeDto.from(summary.type),
                publishedAt = summary.publishedAt?.toString(),
            )
    }
}

@Serializable
data class MetadataDto(
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val draft: Boolean = false,
    val properties: Map<String, String> = emptyMap(),
) {
    companion object {
        fun from(metadata: Metadata) =
            MetadataDto(
                description = metadata.description,
                tags = metadata.tags,
                draft = metadata.draft,
                properties = metadata.properties.mapValues { it.value.toString() },
            )
    }
}
