package io.github.cekrem.content

interface ContentStorage {
    fun getContentTypes(): Set<ContentType>
    fun getByPath(path: String): Content?
    fun getByType(type: ContentType): List<Content>
}
