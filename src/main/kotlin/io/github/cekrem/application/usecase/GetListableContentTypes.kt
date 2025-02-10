package io.github.cekrem.application.usecase

import io.github.cekrem.application.gateway.ContentSource
import io.github.cekrem.domain.model.ContentType

class GetListableContentTypes(
    private val contentSource: ContentSource,
) : UseCase<Unit, Set<ContentType>> {
    override suspend fun invoke(input: Unit): Set<ContentType> =
        contentSource
            .getAvailableTypes()
            .filter { it.listable }
            .toSet()
}
