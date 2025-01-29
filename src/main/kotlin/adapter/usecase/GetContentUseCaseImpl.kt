package io.github.cekrem.adapter.usecase

import io.github.cekrem.application.usecase.GetContentUseCase
import io.github.cekrem.content.internal.ContentGateway
import io.github.cekrem.domain.model.Content

internal class GetContentUseCaseImpl(
    private val contentGateway: ContentGateway,
) : GetContentUseCase {
    override fun invoke(input: String): Content? = contentGateway.getByPath(input)
}
