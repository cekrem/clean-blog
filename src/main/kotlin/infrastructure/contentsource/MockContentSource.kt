package infrastructure.contentsource

import application.gateway.ContentSource
import domain.model.Content
import domain.model.ContentSummary
import domain.model.ContentType

internal class MockContentSource(
    private val contentTypes: Set<ContentType> = emptySet(),
    private val contents: Map<String, Content> = emptyMap(),
    private val contentSummaries: Map<ContentType, List<ContentSummary>> = emptyMap(),
) : ContentSource {
    override fun getAvailableTypes(): Set<ContentType> = contentTypes

    override fun getByPath(path: String): Content? = contents[path]

    override fun getSummariesByType(type: ContentType): List<ContentSummary> = contentSummaries[type] ?: emptyList()
}
