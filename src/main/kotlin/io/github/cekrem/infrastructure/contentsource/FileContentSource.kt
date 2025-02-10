package io.github.cekrem.infrastructure.contentsource

import io.github.cekrem.application.gateway.ContentSource
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.parser.ContentParser

internal class FileContentSource(
    contentRoot: String,
    parser: ContentParser,
) : ContentSource {
    override fun getByPath(path: String): Content? {
        TODO("Not yet implemented")
    }

    override fun getSummariesByType(type: ContentType): List<ContentSummary> {
        TODO("Not yet implemented")
    }

    override fun getAvailableTypes(): Set<ContentType> {
        TODO("Not yet implemented")
    }
}
