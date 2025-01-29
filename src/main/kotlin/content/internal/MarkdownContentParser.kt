package io.github.cekrem.content.internal

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentType

internal class MarkdownContentParser : ContentParser {
    override fun parse(
        rawContent: String,
        path: String,
        type: ContentType,
    ): Content {
        TODO("Not yet implemented")
    }
}
