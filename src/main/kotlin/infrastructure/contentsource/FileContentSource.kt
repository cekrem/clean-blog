package infrastructure.contentsource

import application.gateway.ContentSource
import domain.model.Content
import domain.model.ContentSummary
import domain.model.ContentType
import domain.parser.ContentParser

internal class FileContentSource
    (contentRoot: String, parser: ContentParser) : ContentSource {
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