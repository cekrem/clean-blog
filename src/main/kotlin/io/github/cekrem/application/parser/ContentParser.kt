package io.github.cekrem.application.parser

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentType

interface ContentParser {
    fun parse(
        rawContent: String,
        path: String,
        type: ContentType,
    ): Content
}
