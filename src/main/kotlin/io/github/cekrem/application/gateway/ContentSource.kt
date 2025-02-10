package io.github.cekrem.application.gateway

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType

interface ContentSource {
    fun getByPath(path: String): Content?

    fun getSummariesByType(type: ContentType): List<ContentSummary>

    fun getAvailableTypes(): Set<ContentType>
}
