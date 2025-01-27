package io.github.cekrem.content.internal

import io.github.cekrem.content.Content
import io.github.cekrem.content.ContentSummary
import io.github.cekrem.content.ContentType

internal interface ContentGateway {
    fun getContentTypes(): Set<ContentType>

    fun getByPath(path: String): Content?

    fun getSummariesByType(type: ContentType): List<ContentSummary>
}
