package io.github.cekrem.infrastructure.contentsource

import io.github.cekrem.application.gateway.ContentSource
import io.github.cekrem.application.parser.ContentParser
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.domain.model.ContentType
import kotlin.io.path.Path
import kotlin.io.path.exists

internal class FileContentSource(
    private val contentRoot: String,
    private val parser: ContentParser,
    private val extension: String,
) : ContentSource {
    override fun getByPath(path: String): Content? {
        val rootPath = Path(contentRoot)
        val indexPath = rootPath.resolve(path).resolve("index.$extension")
        val directPath = rootPath.resolve("$path.$extension")

        // Prefer index file if it exists
        val contentPath =
            when {
                indexPath.exists() -> indexPath
                directPath.exists() -> directPath
                else -> return null
            }

        val content = contentPath.toFile().readText()

        val type =
            ContentType(
                name = path.split("/").first(),
                listable = true, // Should it always be listable?
            )

        return parser.parse(rawContent = content, path = path, type = type)
    }

    override fun getSummariesByType(type: ContentType): List<ContentSummary> {
        val typeDirectory = Path(contentRoot).resolve(type.name).toFile()

        if (!typeDirectory.exists() || !typeDirectory.isDirectory) {
            return emptyList()
        }

        return typeDirectory
            .listFiles()
            .mapNotNull { file ->
                // Construct the relative path by combining type name and file name without extension
                val relativePath = "${type.name}/${file.nameWithoutExtension}"
                getByPath(relativePath)
            }.map {
                ContentSummary(
                    title = it.title,
                    path = it.path,
                    type = type,
                    publishedAt = it.metadata.publishedAt,
                )
            }.sortedByDescending { it.publishedAt }
    }

    override fun getAvailableTypes(): Set<ContentType> =
        Path(contentRoot)
            .toFile()
            .listFiles()
            .filter { it.isDirectory }
            .map {
                ContentType(
                    name = it.name,
                    listable = true,
                )
            }.toSet()
}
