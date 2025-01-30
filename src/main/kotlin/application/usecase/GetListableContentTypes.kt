package application.usecase

import domain.model.ContentType

interface GetListableContentTypes : UseCase<Unit, Set<ContentType>> {
    override suspend operator fun invoke(input: Unit): Set<ContentType>
}