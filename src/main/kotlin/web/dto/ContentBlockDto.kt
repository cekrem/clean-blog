package io.github.cekrem.web.dto

import io.github.cekrem.content.ContentBlock
import kotlinx.serialization.Serializable

@Serializable
sealed interface ContentBlockDto {
    @Serializable
    data class Heading(
        val text: String,
        val level: Int = 1,
    ) : ContentBlockDto

    @Serializable
    data class Text(
        val content: String,
    ) : ContentBlockDto

    @Serializable
    data class Code(
        val content: String,
        val language: String? = null,
    ) : ContentBlockDto

    @Serializable
    data class Quote(
        val content: String,
        val attribution: String? = null,
    ) : ContentBlockDto

    @Serializable
    data class Link(
        val text: String,
        val url: String,
        val external: Boolean = false,
    ) : ContentBlockDto

    @Serializable
    data class Image(
        val url: String,
        val alt: String? = null,
        val caption: String? = null,
    ) : ContentBlockDto

    companion object {
        fun from(block: ContentBlock): ContentBlockDto =
            when (block) {
                is ContentBlock.Heading -> Heading(block.text, block.level)
                is ContentBlock.Text -> Text(block.content)
                is ContentBlock.Code -> Code(block.content, block.language)
                is ContentBlock.Quote -> Quote(block.content, block.attribution)
                is ContentBlock.Link -> Link(block.text, block.url, block.external)
                is ContentBlock.Image -> Image(block.url, block.alt, block.caption)
            }
    }
}
