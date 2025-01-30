package application.usecase

import domain.model.ContentSummary
import domain.model.ContentType

interface ListContentsByTypeUseCase : UseCase<ContentType, List<ContentSummary>> {
    override suspend operator fun invoke(type: ContentType): List<ContentSummary>
}