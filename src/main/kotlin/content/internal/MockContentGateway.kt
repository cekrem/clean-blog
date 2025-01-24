package io.github.cekrem.content.internal

import io.github.cekrem.content.Content
import io.github.cekrem.content.ContentGateway
import io.github.cekrem.content.ContentSummary
import io.github.cekrem.content.ContentType

internal class MockContentGateway(
    private val contentTypes: Set<ContentType> = emptySet(),
    private val contents: Map<String, Content> = emptyMap(),
    private val contentSummaries: Map<ContentType, List<ContentSummary>> = emptyMap(),
) : ContentGateway {
    override fun getContentTypes(): Set<ContentType> = contentTypes

    override fun getByPath(path: String): Content? = contents[path]

    override fun getSummariesByType(type: ContentType): List<ContentSummary> = contentSummaries[type] ?: emptyList()
}
