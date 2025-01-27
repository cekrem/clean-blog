package io.github.cekrem.content.usecase

import io.github.cekrem.content.ContentSummary
import io.github.cekrem.content.ContentType
import io.github.cekrem.content.internal.ContentGateway
import io.github.cekrem.usecase.UseCase

interface ListContentsByTypeUseCase : UseCase<ContentType, List<ContentSummary>> {
    override operator fun invoke(type: ContentType): List<ContentSummary>

    companion object {
        internal fun createFromContentGateway(contentGateway: ContentGateway): ListContentsByTypeUseCase =
            object : ListContentsByTypeUseCase {
                override fun invoke(type: ContentType): List<ContentSummary> = contentGateway.getSummariesByType(type)
            }
    }
}
