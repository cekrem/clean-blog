package infrastructure.factory

import application.gateway.ContentSource
import domain.model.Content
import domain.model.ContentSummary
import domain.model.ContentType
import infrastructure.contentsource.FileContentSource
import infrastructure.contentsource.MockContentSource
import infrastructure.contentsource.RssContentSource
import infrastructure.parser.MarkdownContentParser

internal fun createRssContentSource(feedUrl: String): ContentSource = RssContentSource(feedUrl = feedUrl)

internal fun createFileGateway(contentRoot: String): ContentSource =
    FileContentSource(contentRoot, parser = MarkdownContentParser())

internal fun createMockGateway(
    contentTypes: Set<ContentType> = emptySet(),
    contents: Map<String, Content> = emptyMap(),
    contentSummaries: Map<ContentType, List<ContentSummary>> = emptyMap(),
): ContentSource = MockContentSource(contentTypes, contents, contentSummaries)
