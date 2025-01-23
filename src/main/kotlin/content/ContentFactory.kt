package io.github.cekrem.content

import io.github.cekrem.content.internal.RssContentGateway
import io.github.cekrem.content.internal.FileContentGateway
import io.github.cekrem.content.internal.MarkdownContentParser

internal fun createRssGateway(feedUrl: String): ContentGateway = RssContentGateway(feedUrl)

internal fun createFileGateway(contentRoot: String): ContentGateway = FileContentGateway(contentRoot, parser = MarkdownContentParser())
