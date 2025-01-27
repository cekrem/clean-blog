package io.github.cekrem.content.internal

import io.github.cekrem.content.Content
import io.github.cekrem.content.ContentSummary
import io.github.cekrem.content.ContentType
import io.github.cekrem.content.internal.ContentGateway

internal class FileContentGateway(
    private val contentRoot: String,
    private val parser: ContentParser,
) : ContentGateway {
    override fun getContentTypes(): Set<ContentType> = TODO()

    override fun getByPath(path: String): Content = TODO()

    override fun getSummariesByType(type: ContentType): List<ContentSummary> = TODO()
}
