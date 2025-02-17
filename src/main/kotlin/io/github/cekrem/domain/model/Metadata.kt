package io.github.cekrem.domain.model

import kotlinx.datetime.LocalDateTime

data class Metadata(
    val description: String? = null,
    val tags: List<String> = emptyList(),
    val publishedAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
    val draft: Boolean = false,
) {
    init {
        require((description?.length ?: 0) <= MAX_DESCRIPTION_LENGTH) {
            "Description cannot be longer than $MAX_DESCRIPTION_LENGTH characters"
        }

        tags.forEach { tag ->
            require(tag.matches(TAG_PATTERN)) {
                "Tag must be 2-30 lowercase letters, numbers, or hyphens"
            }
        }

        if (publishedAt != null && updatedAt != null) {
            require(updatedAt >= publishedAt) {
                "Updated date cannot be earlier than published date"
            }
        }
    }

    fun isPublished(now: LocalDateTime): Boolean = !draft && (publishedAt?.let { it <= now } ?: true)

    companion object {
        const val MAX_DESCRIPTION_LENGTH = 500
        private val TAG_PATTERN = Regex("^[a-z0-9-]{2,30}$")
    }
}
