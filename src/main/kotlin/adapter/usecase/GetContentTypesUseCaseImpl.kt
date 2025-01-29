package io.github.cekrem.adapter.usecase

import io.github.cekrem.application.usecase.GetContentTypesUseCase
import io.github.cekrem.content.internal.ContentGateway
import io.github.cekrem.domain.model.ContentType

internal class GetContentTypesUseCaseImpl(
    private val contentGateway: ContentGateway,
) : GetContentTypesUseCase {
    override fun invoke(input: Unit): Set<ContentType> = contentGateway.getContentTypes()
}
