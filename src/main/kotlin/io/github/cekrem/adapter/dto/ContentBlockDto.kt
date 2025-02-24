package io.github.cekrem.adapter.dto

import io.github.cekrem.domain.model.ContentBlock
import io.github.cekrem.domain.model.RichText

sealed class ContentBlockDto(
    open val blockTypes: Map<String, Boolean> = emptyMap(),
    open val properties: Map<String, Any?> = emptyMap(),
) {
    data class Heading(
        val text: String,
        val level: Int,
    ) : ContentBlockDto(
            blockTypes = mapOf("heading" to true),
            properties =
                mapOf(
                    "text" to text,
                    "level" to level,
                ),
        )

    data class Text(
        val segments: List<RichTextDto>,
    ) : ContentBlockDto(
            blockTypes = mapOf("text" to true),
            properties = mapOf("segments" to segments),
        )

    data class TextList(
        val items: List<String>,
        val ordered: Boolean,
    ) : ContentBlockDto(
            blockTypes = mapOf("textList" to true),
            properties =
                mapOf(
                    "items" to items,
                    "ordered" to ordered,
                ),
        )

    data class Code(
        val content: String,
        val language: String?,
    ) : ContentBlockDto(
            blockTypes = mapOf("code" to true),
            properties =
                mapOf(
                    "content" to content,
                    "language" to language,
                ),
        )

    data class Quote(
        val content: String,
        val attribution: String?,
    ) : ContentBlockDto(
            blockTypes = mapOf("quote" to true),
            properties =
                mapOf(
                    "content" to content,
                    "attribution" to attribution,
                ),
        )

    data class Link(
        val text: String,
        val url: String,
        val external: Boolean,
    ) : ContentBlockDto(
            blockTypes = mapOf("link" to true),
            properties =
                mapOf(
                    "text" to text,
                    "url" to url,
                    "external" to external,
                ),
        )

    data class Image(
        val url: String,
        val alt: String?,
        val caption: String?,
    ) : ContentBlockDto(
            blockTypes = mapOf("image" to true),
            properties =
                mapOf(
                    "url" to url,
                    "alt" to alt,
                    "caption" to caption,
                ),
        )
}

sealed class RichTextDto(
    open val textTypes: Map<String, Boolean> = emptyMap(),
    open val properties: Map<String, Any?> = emptyMap(),
) {
    data class Plain(
        val text: String,
    ) : RichTextDto(
            textTypes = mapOf("plain" to true),
            properties = mapOf("text" to text),
        )

    data class InlineLink(
        val text: String,
        val url: String,
    ) : RichTextDto(
            textTypes = mapOf("inlineLink" to true),
            properties =
                mapOf(
                    "text" to text,
                    "url" to url,
                ),
        )
}

fun ContentBlock.toDto(): ContentBlockDto =
    when (this) {
        is ContentBlock.Heading -> ContentBlockDto.Heading(text, level)
        is ContentBlock.Text -> ContentBlockDto.Text(segments.map { it.toDto() })
        is ContentBlock.Code -> ContentBlockDto.Code(content, language)
        is ContentBlock.Quote -> ContentBlockDto.Quote(content, attribution)
        is ContentBlock.Link -> ContentBlockDto.Link(text, url, external)
        is ContentBlock.Image -> ContentBlockDto.Image(url, alt, caption)
        is ContentBlock.TextList -> ContentBlockDto.TextList(items, ordered)
    }

private fun RichText.toDto(): RichTextDto =
    when (this) {
        is RichText.Plain -> RichTextDto.Plain(text)
        is RichText.InlineLink -> RichTextDto.InlineLink(text, url)
    }
