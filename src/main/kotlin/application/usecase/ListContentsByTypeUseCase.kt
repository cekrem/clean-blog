package io.github.cekrem.application.usecase

import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType

interface ListContentsByTypeUseCase : UseCase<ContentType, List<ContentSummary>> {
    override operator fun invoke(type: ContentType): List<ContentSummary>
}