package io.github.cekrem.application.usecase

interface UseCase<in I, out O> {
    suspend operator fun invoke(input: I): O
}
