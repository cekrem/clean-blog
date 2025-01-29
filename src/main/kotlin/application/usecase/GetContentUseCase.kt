package io.github.cekrem.application.usecase

import io.github.cekrem.domain.model.Content

interface GetContentUseCase : UseCase<String, Content?> {
    override operator fun invoke(input: String): Content?
}