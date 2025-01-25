package io.github.cekrem.content.usecase

import io.github.cekrem.content.ContentGateway
import io.github.cekrem.content.ContentSummary
import io.github.cekrem.content.ContentType
import io.github.cekrem.usecase.UseCase

class ListContentsByType(
    private val contentGateway: ContentGateway,
) : UseCase<ContentType, List<ContentSummary>> {
    override fun invoke(type: ContentType): List<ContentSummary> = contentGateway.getSummariesByType(type)
}
