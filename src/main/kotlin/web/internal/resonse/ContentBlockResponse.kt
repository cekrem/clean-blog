package io.github.cekrem.web.internal.response

import io.github.cekrem.content.ContentBlock
import kotlinx.serialization.Serializable

@Serializable
sealed interface ContentBlockResponse {
    @Serializable
    data class Heading(
        val text: String,
        val level: Int = 1,
    ) : ContentBlockResponse

    @Serializable
    data class Text(
        val content: String,
    ) : ContentBlockResponse

    @Serializable
    data class Code(
        val content: String,
        val language: String? = null,
    ) : ContentBlockResponse

    @Serializable
    data class Quote(
        val content: String,
        val attribution: String? = null,
    ) : ContentBlockResponse

    @Serializable
    data class Link(
        val text: String,
        val url: String,
        val external: Boolean = false,
    ) : ContentBlockResponse

    @Serializable
    data class Image(
        val url: String,
        val alt: String? = null,
        val caption: String? = null,
    ) : ContentBlockResponse

    companion object {
        fun from(block: ContentBlock): ContentBlockResponse =
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
