package infrastructure.web.internal.template

import domain.model.Content
import domain.model.ContentBlock

// Should match resources/templates/content.mustache
fun Content.toTemplateData(): Map<String, Any?> =
    mapOf(
        "title" to title,
        "metadata" to
                mapOf(
                    "description" to metadata.description,
                ),
        "blocks" to
                blocks.map { block ->
                    when (block) {
                        is ContentBlock.Heading -> mapOf("Heading" to block)
                        is ContentBlock.Text -> mapOf("Text" to block)
                        is ContentBlock.Code -> mapOf("Code" to block)
                        is ContentBlock.Quote -> mapOf("Quote" to block)
                        is ContentBlock.Link -> mapOf("Link" to block)
                        is ContentBlock.Image -> mapOf("Image" to block)
                    }
                },
    )
