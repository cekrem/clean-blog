package infrastructure.usecase

import application.gateway.ContentSource
import application.usecase.GetListableContentTypes
import domain.model.ContentType

internal class GetListableContentTypesImpl(
    private val contentSource: ContentSource,
) : GetListableContentTypes {
    override suspend fun invoke(input: Unit): Set<ContentType> =
        contentSource.getAvailableTypes().filter { it.listable }.toSet()
}
