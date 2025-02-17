package io.github.cekrem.domain.model

sealed interface ContentBlock {
    data class Heading(
        val text: String,
        val level: Int = 1,
    ) : ContentBlock {
        init {
            require(level in 1..6) { "Heading level must be between 1 and 6" }
            require(text.isNotBlank()) { "Heading text cannot be empty or blank" }
        }
    }

    data class Text(
        val segments: List<RichText>,
    ) : ContentBlock {
        init {
            require(segments.isNotEmpty()) { "Text must contain at least one segment" }
        }
    }

    data class Code(
        val content: String,
        val language: String? = null,
    ) : ContentBlock {
        init {
            require(content.isNotBlank()) { "Code content cannot be empty or blank" }
            language?.let {
                require(it.matches(Regex("^[a-z0-9-]+$"))) {
                    "Language must contain only lowercase letters, numbers, and hyphens"
                }
            }
        }
    }

    data class Quote(
        val content: String,
        val attribution: String? = null,
    ) : ContentBlock {
        init {
            require(content.isNotBlank()) { "Quote content cannot be empty or blank" }
        }
    }

    data class Link(
        val text: String,
        val url: String,
        val external: Boolean = false,
    ) : ContentBlock {
        init {
            require(text.isNotBlank()) { "Link text cannot be empty or blank" }
            require(url.isNotBlank()) { "URL cannot be empty or blank" }
            require(isValidUrl(url)) { "Invalid URL format" }
        }
    }

    data class Image(
        val url: String,
        val alt: String? = null,
        val caption: String? = null,
    ) : ContentBlock {
        init {
            require(url.isNotBlank()) { "URL cannot be empty or blank" }
            require(isValidUrl(url)) { "Invalid URL format" }
        }
    }

    companion object {
        private fun isValidUrl(url: String): Boolean =
            url.length < 500 &&
                (
                    url.startsWith("http://") ||
                        url.startsWith("https://") ||
                        url.startsWith("//") ||
                        url.startsWith("/")
                )
    }
}
