package io.github.cekrem.content.usecase

import io.github.cekrem.content.ContentGateway
import io.github.cekrem.content.ContentType
import io.github.cekrem.usecase.NoInputUseCase

class GetContentTypes(
    private val contentGateway: ContentGateway,
) : NoInputUseCase<Set<ContentType>> {
    override fun execute(): Set<ContentType> = contentGateway.getContentTypes()
}
