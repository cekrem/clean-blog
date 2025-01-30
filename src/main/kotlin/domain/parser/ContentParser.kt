package domain.parser

import domain.model.Content
import domain.model.ContentType

interface ContentParser {
    fun parse(
        rawContent: String,
        path: String,
        type: ContentType,
    ): Content
}