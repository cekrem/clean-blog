package io.github.cekrem.domain.model

data class ContentType(
    val name: String, // e.g., "posts", "pages", "events"
    val listable: Boolean,
) {
    val singular: String = name.removeSuffix("s")

    init {
        require(name.matches(NAME_PATTERN)) {
            "Content type name must be lowercase letters only"
        }
    }

    companion object {
        private val NAME_PATTERN = Regex("^[a-z]+$")
    }
}
