package infrastructure.usecase

import application.gateway.ContentSource
import application.usecase.ListContentsByTypeUseCase
import domain.model.ContentSummary
import domain.model.ContentType

internal class ListContentsByTypeUseCaseImpl(
    private val contentSource: ContentSource,
) : ListContentsByTypeUseCase {
    override suspend fun invoke(type: ContentType): List<ContentSummary> = contentSource.getSummariesByType(type)
}
