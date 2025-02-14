package io.github.cekrem.domain.model

sealed interface RichText {
    data class Plain(
        val text: String,
    ) : RichText

    data class InlineLink(
        val text: String,
        val url: String,
        val external: Boolean = false,
    ) : RichText
}
