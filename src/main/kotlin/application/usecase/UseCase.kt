package io.github.cekrem.application.usecase

interface UseCase<in I, out O> {
    operator fun invoke(input: I): O
}
