package io.github.cekrem.application.usecase

import io.github.cekrem.domain.model.ContentType

interface GetContentTypesUseCase : UseCase<Unit, Set<ContentType>>