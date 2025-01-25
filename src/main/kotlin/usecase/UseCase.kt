package io.github.cekrem.usecase

interface UseCase<in I, out O> {
    operator fun invoke(input: I): O
}

// For use cases that don't need input
interface NoInputUseCase<out O> : UseCase<Unit, O> {
    fun execute(): O

    override fun invoke(input: Unit): O = execute()
}

// For use cases that don't produce output
interface NoOutputUseCase<in I> : UseCase<I, Unit> {
    fun execute(input: I)

    override fun invoke(input: I) = execute(input)
} 
