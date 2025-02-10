package io.github.cekrem.application.usecase

import io.github.cekrem.application.gateway.ContentSource
import io.github.cekrem.domain.model.Content

class GetContentUseCase(
    private val contentSource: ContentSource,
) : UseCase<String, Content?> {
    override suspend fun invoke(input: String): Content? = contentSource.getByPath(input)
}
