package io.github.cekrem.content

interface ContentGateway {
    fun getContentTypes(): Set<ContentType>

    fun getByPath(path: String): Content?

    fun getSummariesByType(type: ContentType): List<ContentSummary>
} 
