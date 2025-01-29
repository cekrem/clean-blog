package io.github.cekrem.content.internal

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType

internal interface ContentGateway {
    fun getContentTypes(): Set<ContentType>

    fun getByPath(path: String): Content?

    fun getSummariesByType(type: ContentType): List<ContentSummary>
}
