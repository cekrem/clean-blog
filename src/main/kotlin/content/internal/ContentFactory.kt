package io.github.cekrem.content.internal

import io.github.cekrem.content.Content
import io.github.cekrem.content.ContentSummary
import io.github.cekrem.content.ContentType
import io.github.cekrem.content.internal.ContentGateway
import io.github.cekrem.content.internal.FileContentGateway
import io.github.cekrem.content.internal.MarkdownContentParser
import io.github.cekrem.content.internal.MockContentGateway
import io.github.cekrem.content.internal.RssContentGateway

internal fun createRssGateway(feedUrl: String): ContentGateway = RssContentGateway(feedUrl)

internal fun createFileGateway(contentRoot: String): ContentGateway = FileContentGateway(contentRoot, parser = MarkdownContentParser())

internal fun createMockGateway(
    contentTypes: Set<ContentType> = emptySet(),
    contents: Map<String, Content> = emptyMap(),
    contentSummaries: Map<ContentType, List<ContentSummary>> = emptyMap(),
): ContentGateway = MockContentGateway(contentTypes, contents, contentSummaries)
