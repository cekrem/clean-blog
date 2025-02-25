package io.github.cekrem.infrastructure.parser

import io.github.cekrem.application.parser.ContentParser
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentBlock
import io.github.cekrem.domain.model.ContentType
import io.github.cekrem.domain.model.Metadata
import io.github.cekrem.domain.model.RichText
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
                .substringAfter(frontMatter.delimiter, "")
                .substringAfter(frontMatter.delimiter, "")

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
        val delimiter =
            delimiters.firstOrNull { rawContent.startsWith(it) }
                ?: throw InvalidFrontMatterException("Could not determine front matter start/end: $rawContent")

        val rawFrontMatter =
            rawContent.split("$delimiter").getOrNull(1)
                ?: throw InvalidFrontMatterException("Could not determine front matter start/end: $rawContent")
        val entries =
            rawFrontMatter
                .split("\n")
                .map { it.split(Regex("[:=]"), 2) }
                .filter { it.size == 2 }
                .associate { (key, value) -> key.trim() to value.trim().removeSurrounding("\"") }

        return FrontMatter(
            delimiter = delimiter,
            title = entries["title"] ?: throw InvalidFrontMatterException("Could not find title"),
            description = entries["description"],
            tags =
                entries["tags"]?.let { tagString ->
                    tagString
                        .trim()
                        .removeSurrounding("[", "]")
                        .split(",")
                        .map { it.trim() }
                        .map { it.removeSurrounding("\"") }
                        .map { it.removeSurrounding("'") }
                        .filter { it.isNotEmpty() }
                } ?: emptyList(),
            draft = entries["draft"]?.toBoolean() == true,
            publishedAt = entries["publishedAt"]?.let(LocalDateTime::parse),
            updatedAt = entries["updatedAt"]?.let(LocalDateTime::parse),
        )
    }

    private data class FrontMatter(
        val delimiter: String,
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
        val delimiters = listOf("---\n", "+++\n")
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
            MarkdownElementTypes.ORDERED_LIST,
            MarkdownElementTypes.UNORDERED_LIST,
            -> parseList(node, markdownContent)

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

        val children = node.children.getOrNull(1)?.children ?: emptyList()

        if (children.size < 3) {
            return ContentBlock.textHeading(
                text = markdownContent.substring(node.startOffset, node.endOffset).trim('#', ' '),
                level = level,
            )
        }

        return ContentBlock.Heading(
            segments = parseRichText(children, markdownContent),
            level = level,
        )
    }

    private fun parseParagraph(
        node: ASTNode,
        markdownContent: String,
    ): ContentBlock {
        if (node.children.size == 1) {
            when (node.children[0].type) {
                MarkdownElementTypes.INLINE_LINK -> return parseLink(node.children[0], markdownContent)
                MarkdownElementTypes.IMAGE -> return parseImage(node.children[0], markdownContent)
            }
        }

        return ContentBlock.Text(
            segments = parseRichText(node.children, markdownContent),
        )
    }

    private fun parseRichText(
        children: List<ASTNode> = emptyList(),
        markdownContent: String,
    ): List<RichText> {
        // Parse text with potential inline links
        val segments = mutableListOf<RichText>()
        var plainText = ""

        children.forEach { child ->
            when (child.type) {
                MarkdownElementTypes.INLINE_LINK -> {
                    // Add accumulated text if any
                    if (plainText.isNotEmpty()) {
                        segments.add(RichText.Plain(plainText))
                        plainText = ""
                    }

                    val link = parseLink(child, markdownContent)
                    segments.add(
                        RichText.InlineLink(
                            text = link.text,
                            url = link.url,
                            external = link.external,
                        ),
                    )
                    plainText = ""
                }

                MarkdownElementTypes.EMPH -> {
                    // Add accumulated text if any
                    if (plainText.isNotEmpty()) {
                        segments.add(RichText.Plain(plainText))
                        plainText = ""
                    }
                    segments.add(
                        RichText.Italic(
                            text =
                                markdownContent
                                    .substring(child.startOffset, child.endOffset)
                                    .removeSurrounding("_")
                                    .removeSurrounding("*"),
                        ),
                    )
                    plainText = ""
                }

                MarkdownElementTypes.STRONG -> {
                    // Add accumulated text if any
                    if (plainText.isNotEmpty()) {
                        segments.add(RichText.Plain(plainText))
                        plainText = ""
                    }
                    segments.add(
                        RichText.Bold(
                            text =
                                markdownContent
                                    .substring(child.startOffset, child.endOffset)
                                    .removeSurrounding("**"),
                        ),
                    )
                    plainText = ""
                }

                MarkdownElementTypes.CODE_SPAN -> {
                    // Add accumulated text if any
                    if (plainText.isNotEmpty()) {
                        segments.add(RichText.Plain(plainText))
                        plainText = ""
                    }
                    segments.add(
                        RichText.InlineCode(
                            text = markdownContent.substring(child.startOffset, child.endOffset).trim('`'),
                        ),
                    )
                    plainText = ""
                }

                else -> {
                    plainText += markdownContent.substring(child.startOffset, child.endOffset)
                    if (segments.isEmpty()) {
                        plainText = plainText.trimStart()
                    }
                }
            }
        }

        // Add any remaining text
        if (plainText.isNotEmpty()) {
            segments.add(RichText.Plain(plainText))
        }

        return segments
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

    private fun parseList(
        node: ASTNode,
        markdownContent: String,
    ): ContentBlock.TextList {
        val items =
            buildList {
                node.children.forEach { itemNode ->
                    if (itemNode.type == MarkdownElementTypes.LIST_ITEM) {
                        val itemText =
                            markdownContent
                                .substring(itemNode.startOffset, itemNode.endOffset)
                                .replace(Regex("^\\s*([*+-]|\\d+[.)])\\s*"), "")
                                .trim()
                        if (itemText.isNotBlank()) {
                            add(itemText)
                        }
                    }
                }
            }

        return ContentBlock.TextList(items = items, ordered = node.type == MarkdownElementTypes.ORDERED_LIST)
    }
}

class InvalidFrontMatterException : Exception {
    constructor() : super()
    constructor(msg: String) : super(msg)
}
