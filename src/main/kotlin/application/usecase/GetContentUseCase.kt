package application.usecase

import domain.model.Content

interface GetContentUseCase : UseCase<String, Content?> {
    override suspend operator fun invoke(input: String): Content?
}