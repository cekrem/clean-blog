package infrastructure.usecase

import application.gateway.ContentSource
import application.usecase.GetContentUseCase
import domain.model.Content

internal class GetContentUseCaseImpl(
    private val contentSource: ContentSource,
) : GetContentUseCase {
    override suspend fun invoke(input: String): Content? = contentSource.getByPath(input)
}
