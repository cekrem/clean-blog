package io.github.cekrem.usecase

interface UseCase<in I, out O> {
    operator fun invoke(input: I): O
}
