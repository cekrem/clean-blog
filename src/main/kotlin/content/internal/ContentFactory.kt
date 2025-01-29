package io.github.cekrem.content.internal

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType

internal fun createRssGateway(feedUrl: String): ContentGateway = RssContentGateway(feedUrl)

internal fun createFileGateway(contentRoot: String): ContentGateway =
    FileContentGateway(contentRoot, parser = MarkdownContentParser())

internal fun createMockGateway(
    contentTypes: Set<ContentType> = emptySet(),
    contents: Map<String, Content> = emptyMap(),
    contentSummaries: Map<ContentType, List<ContentSummary>> = emptyMap(),
): ContentGateway = MockContentGateway(contentTypes, contents, contentSummaries)
