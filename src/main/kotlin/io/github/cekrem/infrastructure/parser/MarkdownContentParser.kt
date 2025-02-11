package io.github.cekrem.infrastructure.parser

import io.github.cekrem.application.parser.ContentParser
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
