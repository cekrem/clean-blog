package io.github.cekrem.infrastructure.factory

import io.github.cekrem.application.gateway.ContentSource
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.infrastructure.contentsource.FileContentSource
import io.github.cekrem.infrastructure.contentsource.MockContentSource
import io.github.cekrem.infrastructure.contentsource.RssContentSource
import io.github.cekrem.infrastructure.parser.MarkdownContentParser

internal fun createRssContentSource(feedUrl: String): ContentSource = RssContentSource(feedUrl = feedUrl)

internal fun createFileGateway(contentRoot: String): ContentSource =
    FileContentSource(
        contentRoot = contentRoot,
        parser = MarkdownContentParser(),
        extension = "md",
    )

internal fun createMockGateway(
    contentTypes: Set<ContentType> = emptySet(),
    contents: Map<String, Content> = emptyMap(),
    contentSummaries: Map<ContentType, List<ContentSummary>> = emptyMap(),
): ContentSource = MockContentSource(contentTypes, contents, contentSummaries)
