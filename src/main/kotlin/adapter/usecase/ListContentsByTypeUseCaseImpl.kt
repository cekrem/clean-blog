package io.github.cekrem.adapter.usecase

import io.github.cekrem.application.usecase.ListContentsByTypeUseCase
import io.github.cekrem.content.internal.ContentGateway
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType

internal class ListContentsByTypeUseCaseImpl(
    private val contentGateway: ContentGateway,
) : ListContentsByTypeUseCase {
    override fun invoke(type: ContentType): List<ContentSummary> = contentGateway.getSummariesByType(type)
}
