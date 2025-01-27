package io.github.cekrem.content.usecase

import io.github.cekrem.content.ContentType
import io.github.cekrem.content.internal.ContentGateway
import io.github.cekrem.usecase.UseCase

interface GetContentTypesUseCase : UseCase<Unit, Set<ContentType>> {
    override operator fun invoke(input: Unit): Set<ContentType>
}

internal class GetContentTypesUseCaseImpl(
    private val contentGateway: ContentGateway,
) : GetContentTypesUseCase {
    override fun invoke(input: Unit): Set<ContentType> = contentGateway.getContentTypes()
}
