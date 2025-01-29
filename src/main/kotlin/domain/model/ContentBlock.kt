package io.github.cekrem.domain.model

sealed interface ContentBlock {
    data class Heading(
        val text: String,
        val level: Int = 1,
    ) : ContentBlock

    data class Text(
        val content: String,
    ) : ContentBlock

    data class Code(
        val content: String,
        val language: String? = null,
    ) : ContentBlock

    data class Quote(
        val content: String,
        val attribution: String? = null,
    ) : ContentBlock

    data class Link(
        val text: String,
        val url: String,
        val external: Boolean = false,
    ) : ContentBlock

    data class Image(
        val url: String,
        val alt: String? = null,
        val caption: String? = null,
    ) : ContentBlock
}