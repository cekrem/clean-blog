package io.github.cekrem.domain.model

sealed interface RichText {
    val text: String

    data class Plain(
        override val text: String,
    ) : RichText

    data class Bold(
        override val text: String,
    ) : RichText

    data class Italic(
        override val text: String,
    ) : RichText

    data class InlineCode(
        override val text: String,
    ) : RichText

    data class InlineLink(
        override val text: String,
        val url: String,
        val external: Boolean,
    ) : RichText
}
