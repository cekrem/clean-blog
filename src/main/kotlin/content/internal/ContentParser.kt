package io.github.cekrem.content.internal

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentType

internal interface ContentParser {
    fun parse(
        rawContent: String,
        path: String,
        type: ContentType,
    ): Content
} 
