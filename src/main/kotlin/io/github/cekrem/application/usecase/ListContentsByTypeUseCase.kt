package io.github.cekrem.application.usecase

import io.github.cekrem.application.gateway.ContentSource
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType

class ListContentsByTypeUseCase(
    private val contentSource: ContentSource,
) : UseCase<ContentType, List<ContentSummary>> {
    override suspend fun invoke(type: ContentType): List<ContentSummary> = contentSource.getSummariesByType(type)
}
