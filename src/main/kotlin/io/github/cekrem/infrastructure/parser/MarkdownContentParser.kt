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

        val tree = markdownTreeParser.buildMarkdownTreeFromString(markdownContent)
        val blocks = blockParser.parseBlocks(tree, markdownContent)

        return Content(
            path = path,
            title = frontMatter.title,
            blocks = blocks,
            type = type,
            metadata = frontMatter.metadata,
        )
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
        val markdownTreeParser = MarkdownParser(markdownFlavor)
        val blockParser = MarkdownBlockParser()
    }
}

private class MarkdownBlockParser {
    fun parseBlocks(
        tree: ASTNode,
        markdownContent: String,
    ): List<ContentBlock> =
        buildList {
            tree.children.forEach { node ->
                parseNode(node, markdownContent)?.let { add(it) }
            }
        }

    private fun parseNode(
        node: ASTNode,
        markdownContent: String,
    ): ContentBlock? =
        when (node.type) {
            MarkdownElementTypes.ATX_1,
            MarkdownElementTypes.ATX_2,
            MarkdownElementTypes.ATX_3,
            MarkdownElementTypes.ATX_4,
            MarkdownElementTypes.ATX_5,
            MarkdownElementTypes.ATX_6,
            -> parseHeading(node, markdownContent)

            MarkdownElementTypes.PARAGRAPH -> parseParagraph(node, markdownContent)
            MarkdownElementTypes.CODE_FENCE -> parseCodeBlock(node, markdownContent)
            MarkdownElementTypes.BLOCK_QUOTE -> parseQuote(node, markdownContent)
            MarkdownElementTypes.IMAGE -> parseImage(node, markdownContent)
            else -> null
        }

    private fun parseHeading(
        node: ASTNode,
        markdownContent: String,
    ): ContentBlock.Heading {
        val level =
            when (node.type) {
                MarkdownElementTypes.ATX_1 -> 1
                MarkdownElementTypes.ATX_2 -> 2
                MarkdownElementTypes.ATX_3 -> 3
                MarkdownElementTypes.ATX_4 -> 4
                MarkdownElementTypes.ATX_5 -> 5
                MarkdownElementTypes.ATX_6 -> 6
                else -> throw IllegalArgumentException("Invalid heading type")
            }
        return ContentBlock.Heading(
            text = markdownContent.substring(node.startOffset, node.endOffset).trim('#', ' '),
            level = level,
        )
    }

    private fun parseParagraph(
        node: ASTNode,
        markdownContent: String,
    ): ContentBlock {
        // Check if paragraph contains a single link or image
        if (node.children.size == 1) {
            when (node.children[0].type) {
                MarkdownElementTypes.INLINE_LINK -> return parseLink(node.children[0], markdownContent)
                MarkdownElementTypes.IMAGE -> return parseImage(node.children[0], markdownContent)
            }
        }

        return ContentBlock.Text(
            content = markdownContent.substring(node.startOffset, node.endOffset).trim(),
        )
    }

    private fun parseCodeBlock(
        node: ASTNode,
        markdownContent: String,
    ): ContentBlock.Code {
        val content = markdownContent.substring(node.startOffset, node.endOffset)
        val lines = content.lines()
        val language =
            lines
                .firstOrNull()
                ?.trim('`')
                ?.takeIf { it.isNotEmpty() }

        // Get content lines (excluding fence lines) and normalize indentation
        val codeLines = lines.drop(1).dropLast(1)
        val normalizedContent = normalizeIndentation(codeLines)

        return ContentBlock.Code(
            content = normalizedContent,
            language = language,
        )
    }

    private fun normalizeIndentation(lines: List<String>): String {
        if (lines.isEmpty()) return ""

        // Find the minimum indentation level (excluding empty lines)
        val minIndent =
            lines
                .filter { it.isNotBlank() }
                .minOfOrNull { line -> line.takeWhile { it.isWhitespace() }.length } ?: 0

        // Remove the common indentation from all lines
        return lines
            .map { line ->
                if (line.isBlank()) line else line.substring(minIndent)
            }.joinToString("\n")
    }

    private fun parseQuote(
        node: ASTNode,
        markdownContent: String,
    ): ContentBlock.Quote {
        val content = markdownContent.substring(node.startOffset, node.endOffset)

        // Split into lines and process
        val lines =
            content
                .lines()
                .map { it.trim('>', ' ') } // Remove '>' and spaces from each line

        // Find attribution if it exists (last line starting with --)
        val (contentLines, attributionLine) =
            if (lines.last().startsWith("--")) {
                lines.dropLast(1) to lines.last().substringAfter("--").trim()
            } else {
                lines to null
            }

        return ContentBlock.Quote(
            content = contentLines.joinToString(" ").trim(),
            attribution = attributionLine,
        )
    }

    private fun parseLink(
        node: ASTNode,
        markdownContent: String,
    ): ContentBlock.Link {
        val content = markdownContent.substring(node.startOffset, node.endOffset)
        val text = content.substringAfter('[').substringBefore(']')
        val url = content.substringAfter('(').substringBefore(')')

        return ContentBlock.Link(
            text = text,
            url = url,
            external = url.startsWith("http") || url.startsWith("//"),
        )
    }

    private fun parseImage(
        node: ASTNode,
        markdownContent: String,
    ): ContentBlock.Image {
        val content = markdownContent.substring(node.startOffset, node.endOffset)
        val alt = content.substringAfter('[').substringBefore(']')
        val urlAndCaption = content.substringAfter('(').substringBefore(')')

        // Extract URL and caption - caption is within quotes after the URL
        val url = urlAndCaption.substringBefore('"').trim()
        val caption =
            urlAndCaption
                .substringAfter('"', "")
                .substringBefore('"', "")
                .takeIf { it.isNotEmpty() }

        return ContentBlock.Image(
            url = url,
            alt = alt.takeIf { it.isNotEmpty() },
            caption = caption,
        )
    }
}

class InvalidFrontMatterException : Exception()
