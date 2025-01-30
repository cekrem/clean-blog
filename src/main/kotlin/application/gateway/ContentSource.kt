package application.gateway

import domain.model.Content
import domain.model.ContentSummary
import domain.model.ContentType

interface ContentSource {
    fun getByPath(path: String): Content?

    fun getSummariesByType(type: ContentType): List<ContentSummary>

    fun getAvailableTypes(): Set<ContentType>
} 
