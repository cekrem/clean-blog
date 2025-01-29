package io.github.cekrem.web.internal.response

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
import kotlinx.serialization.Serializable

@Serializable
data class ContentTypeResponse(
    val name: String,
    val singular: String,
    val listable: Boolean,
) {
    companion object {
        fun from(type: ContentType) =
            ContentTypeResponse(
                name = type.name,
                singular = type.singular,
                listable = type.listable,
            )
    }
}

@Serializable
data class ContentResponse(
    val path: String,
    val title: String,
    val blocks: List<ContentBlockResponse>,
    val type: ContentTypeResponse,
    val metadata: MetadataResponse,
    val publishedAt: String? = null,
    val updatedAt: String? = null,
    val slug: String,
) {
    companion object {
        fun from(content: Content) =
            ContentResponse(
                path = content.path,
                title = content.title,
                blocks = content.blocks.map { ContentBlockResponse.from(it) },
                type = ContentTypeResponse.from(content.type),
                metadata = MetadataResponse.from(content.metadata),
                publishedAt = content.publishedAt?.toString(),
                updatedAt = content.updatedAt?.toString(),
                slug = content.slug,
            )
    }
}

@Serializable
data class ContentSummaryResponse(
    val path: String,
    val title: String,
    val type: ContentTypeResponse,
    val publishedAt: String? = null,
) {
    companion object {
        fun from(summary: ContentSummary) =
            ContentSummaryResponse(
                path = summary.path,
                title = summary.title,
                type = ContentTypeResponse.from(summary.type),
                publishedAt = summary.publishedAt?.toString(),
            )
    }
}

@Serializable
data class MetadataResponse(
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val draft: Boolean = false,
    val properties: Map<String, String> = emptyMap(),
) {
    companion object {
        fun from(metadata: Metadata) =
            MetadataResponse(
                description = metadata.description,
                tags = metadata.tags,
                draft = metadata.draft,
                properties = metadata.properties.mapValues { it.value.toString() },
            )
    }
}
