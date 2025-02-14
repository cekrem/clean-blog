package io.github.cekrem.infrastructure.parser

import io.github.cekrem.application.parser.ContentParser
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentBlock
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
import kotlinx.datetime.LocalDateTime
import org.intellij.markdown.MarkdownElementTypes
import org.intellij.markdown.ast.ASTNode
import org.intellij.markdown.flavours.commonmark.CommonMarkFlavourDescriptor
import org.intellij.markdown.parser.MarkdownParser

internal class MarkdownContentParser : ContentParser {
    override fun parse(
        rawContent: String,
        path: String,
        type: ContentType,
    ): Content {
        val frontMatter = parseFrontMatter(rawContent)

        val markdownContent =
            rawContent
                .substringAfter("---\n", "")
                .substringAfter("---\n", "")

        val tree = markdownParserImpl.buildMarkdownTreeFromString(markdownContent)

        val blocks =
            mapTreeToBlocks(tree = tree, markdownContent = markdownContent)

        return Content(
            path = path,
            title = frontMatter.title,
            blocks = blocks,
            type = type,
            metadata = frontMatter.metadata,
        )
    }

    @Suppress("LongMethod")
    private fun mapTreeToBlocks(
        tree: ASTNode,
        markdownContent: String,
    ): List<ContentBlock> =
        tree.children.mapNotNull { node ->
            when (node.type) {
                // Headers
                MarkdownElementTypes.ATX_1 ->
                    ContentBlock.Heading(
                        text = markdownContent.substring(node.startOffset, node.endOffset).trim('#', ' '),
                        level = 1,
                    )

                MarkdownElementTypes.ATX_2 ->
                    ContentBlock.Heading(
                        text = markdownContent.substring(node.startOffset, node.endOffset).trim('#', ' '),
                        level = 2,
                    )

                MarkdownElementTypes.ATX_3 ->
                    ContentBlock.Heading(
                        text = markdownContent.substring(node.startOffset, node.endOffset).trim('#', ' '),
                        level = 3,
                    )

                MarkdownElementTypes.ATX_4 ->
                    ContentBlock.Heading(
                        text = markdownContent.substring(node.startOffset, node.endOffset).trim('#', ' '),
                        level = 4,
                    )

                MarkdownElementTypes.ATX_5 ->
                    ContentBlock.Heading(
                        text = markdownContent.substring(node.startOffset, node.endOffset).trim('#', ' '),
                        level = 5,
                    )

                MarkdownElementTypes.ATX_6 ->
                    ContentBlock.Heading(
                        text = markdownContent.substring(node.startOffset, node.endOffset).trim('#', ' '),
                        level = 6,
                    )

                // Text content
                MarkdownElementTypes.PARAGRAPH ->
                    ContentBlock.Text(
                        content = markdownContent.substring(node.startOffset, node.endOffset).trim(),
                    )

                // Code blocks
                MarkdownElementTypes.CODE_FENCE -> {
                    val content = markdownContent.substring(node.startOffset, node.endOffset)
                    val lines = content.lines()
                    val language =
                        lines
                            .firstOrNull()
                            ?.trim('`')
                            ?.takeIf { it.isNotEmpty() }
                    ContentBlock.Code(
                        content = lines.drop(1).dropLast(1).joinToString("\n"),
                        language = language,
                    )
                }

                // Quotes
                MarkdownElementTypes.BLOCK_QUOTE -> {
                    val content = markdownContent.substring(node.startOffset, node.endOffset)
                    // Look for attribution pattern: -- Author or > -- Author
                    val parts = content.split(Regex("""\s*--\s*"""), limit = 2)
                    ContentBlock.Quote(
                        content = parts[0].trim('>', ' '),
                        attribution = parts.getOrNull(1)?.trim(),
                    )
                }

                // Links
                MarkdownElementTypes.LINK_DEFINITION -> {
                    val content = markdownContent.substring(node.startOffset, node.endOffset)
                    // Parse [text](url) format
                    val text = content.substringAfter('[').substringBefore(']')
                    val url = content.substringAfter('(').substringBefore(')')
                    ContentBlock.Link(
                        text = text,
                        url = url,
                        external = url.startsWith("http") || url.startsWith("//"),
                    )
                }

                // Images
                MarkdownElementTypes.IMAGE -> {
                    val content = markdownContent.substring(node.startOffset, node.endOffset)
                    // Parse ![alt](url "caption") format
                    val alt = content.substringAfter('[').substringBefore(']')
                    val urlAndCaption = content.substringAfter('(').substringBefore(')')
                    val parts = urlAndCaption.split(Regex("""\s+"([^"]+)""""))
                    ContentBlock.Image(
                        url = parts[0].trim(),
                        alt = alt.takeIf { it.isNotEmpty() },
                        caption = parts.getOrNull(1)?.trim('"'),
                    )
                }

                else -> null // Ignore unsupported elements
            }
        }

    private fun parseFrontMatter(rawContent: String): FrontMatter {
        val rawFrontMatter = rawContent.split("---\n").getOrNull(1) ?: throw InvalidFrontMatterException()
        val entries =
            rawFrontMatter.split("\n").associate {
                it.substringBefore(":").trim() to
                    it.substringAfter(":").trim()
            }

        return FrontMatter(
            title = entries["title"] ?: throw InvalidFrontMatterException(),
            description = entries["description"],
            tags =
                entries["tags"]
                    ?.removeSurrounding("[", "]")
                    ?.split(Regex("\\s*,\\s*"))
                    ?.map { it.removeSurrounding("\"") }
                    ?.map { it.removeSurrounding("'") }
                    ?: emptyList(),
            draft = entries["draft"]?.toBoolean() == true,
            publishedAt = entries["publishedAt"]?.let(LocalDateTime::parse),
            updatedAt = entries["updatedAt"]?.let(LocalDateTime::parse),
        )
    }

    private data class FrontMatter(
        val title: String,
        val description: String?,
        val tags: List<String> = emptyList(),
        val draft: Boolean = false,
        val publishedAt: LocalDateTime? = null,
        val updatedAt: LocalDateTime? = null,
    ) {
        val metadata =
            Metadata(
                description = description,
                tags = tags,
                draft = draft,
                publishedAt = publishedAt,
                updatedAt = updatedAt,
            )
    }

    private companion object {
        val markdownFlavor = CommonMarkFlavourDescriptor()
        val markdownParserImpl = MarkdownParser(markdownFlavor)
    }
}

class InvalidFrontMatterException : Exception()
