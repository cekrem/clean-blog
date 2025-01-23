package io.github.cekrem.content.internal

import io.github.cekrem.content.Content
import io.github.cekrem.content.ContentType

internal interface ContentParser {
    fun parse(
        rawContent: String,
        path: String,
        type: ContentType,
    ): Content
} 
