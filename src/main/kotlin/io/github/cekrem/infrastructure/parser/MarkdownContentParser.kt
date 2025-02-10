package io.github.cekrem.infrastructure.parser

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.parser.ContentParser

internal class MarkdownContentParser : ContentParser {
    override fun parse(
        rawContent: String,
        path: String,
        type: ContentType,
    ): Content {
        TODO("Not yet implemented")
    }
}
