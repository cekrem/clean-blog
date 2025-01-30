package infrastructure.parser

import domain.model.Content
import domain.model.ContentType
import domain.parser.ContentParser

internal class MarkdownContentParser : ContentParser {
    override fun parse(
        rawContent: String,
        path: String,
        type: ContentType,
    ): Content {
        TODO("Not yet implemented")
    }
}
