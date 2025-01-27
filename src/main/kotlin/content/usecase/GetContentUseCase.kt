package io.github.cekrem.content.usecase

import io.github.cekrem.content.Content
import io.github.cekrem.content.internal.ContentGateway
import io.github.cekrem.usecase.UseCase

interface GetContentUseCase : UseCase<String, Content?> {
    override operator fun invoke(input: String): Content?

    companion object {
        internal fun createFromContentGateway(contentGateway: ContentGateway): GetContentUseCase =
            object : GetContentUseCase {
                override fun invoke(input: String): Content? = contentGateway.getByPath(input)
            }
    }
}
