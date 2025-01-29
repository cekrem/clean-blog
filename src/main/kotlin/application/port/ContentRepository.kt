package io.github.cekrem.application.port

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentType

interface ContentRepository {
    fun findByPath(path: String): Content?

    fun findAllByType(type: ContentType): List<Content>

    fun getAvailableTypes(): Set<ContentType>
} 
