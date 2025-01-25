package io.github.cekrem.content.usecase

import io.github.cekrem.content.Content
import io.github.cekrem.content.ContentGateway
import io.github.cekrem.usecase.UseCase

class GetContent(
    private val contentGateway: ContentGateway,
) : UseCase<String, Content?> {
    override fun invoke(path: String): Content? = contentGateway.getByPath(path)
}
