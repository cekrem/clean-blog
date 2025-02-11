package io.github.cekrem.domain.model

data class Content(
    val path: String,
    val title: String,
    val blocks: List<ContentBlock>,
    val type: ContentType,
    val metadata: Metadata,
    val slug: String = path.split("/").last(),
)
