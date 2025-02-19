package io.github.cekrem.adapter.dto

import io.github.cekrem.domain.model.ContentBlock
import io.github.cekrem.domain.model.RichText

data class ContentBlockDto(
    val isHeading: Boolean = false,
    val isText: Boolean = false,
    val isList: Boolean = false,
    val isCode: Boolean = false,
    val isTextWithLink: Boolean = false,
    val text: String? = null,
    val level: Int? = null,
    val segments: List<RichTextDto>? = null,
    val items: List<String>? = null,
    val content: String? = null,
    val language: String? = null,
    val url: String? = null,
    val linkText: String? = null,
)

data class RichTextDto(
    val isPlain: Boolean = false,
    val isInlineLink: Boolean = false,
    val text: String,
    val url: String? = null,
)

fun ContentBlock.toDto() =
    when (this) {
        is ContentBlock.Heading ->
            ContentBlockDto(
                isHeading = true,
                text = text,
                level = level,
            )
        is ContentBlock.Text ->
            ContentBlockDto(
                isText = true,
                segments = segments.map { it.toDto() },
            )
        is ContentBlock.Code ->
            ContentBlockDto(
                isCode = true,
                content = content,
                language = language,
            )
        is ContentBlock.Quote ->
            ContentBlockDto(
                text = content,
                linkText = attribution,
            )
        is ContentBlock.Link ->
            ContentBlockDto(
                isTextWithLink = true,
                text = text,
                url = url,
                linkText = text,
            )
        is ContentBlock.Image ->
            ContentBlockDto(
                url = url,
                text = alt,
                linkText = caption,
            )
    }

private fun RichText.toDto() =
    when (this) {
        is RichText.Plain ->
            RichTextDto(
                isPlain = true,
                text = text,
            )
        is RichText.InlineLink ->
            RichTextDto(
                isInlineLink = true,
                text = text,
                url = url,
            )
    }
