package io.github.cekrem.content.usecase

import io.github.cekrem.content.ContentGateway
import io.github.cekrem.content.ContentType
import io.github.cekrem.usecase.UseCase

class GetContentTypes(
    private val contentGateway: ContentGateway,
) : UseCase<Unit, Set<ContentType>> {
    override fun invoke(input: Unit): Set<ContentType> = contentGateway.getContentTypes()
}
