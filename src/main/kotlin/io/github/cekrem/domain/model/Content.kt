package io.github.cekrem.domain.model

data class Content(
    val path: String,
    val title: String,
    val blocks: List<ContentBlock>,
    val type: ContentType,
    val metadata: Metadata,
) {
    val slug: String = path.split("/").last()

    init {
        require(title.isNotBlank()) { "Title cannot be empty" }
        validatePath()
        validatePathMatchesType()
    }

    private fun validatePath() {
        require(!path.startsWith("/")) { "Path cannot start with slash" }
        require(!path.endsWith("/")) { "Path cannot end with slash" }
        require(!path.contains("//")) { "Path cannot contain double slashes" }
        require(!path.contains(" ")) { "Path cannot contain spaces" }

        val parts = path.split("/")
        require(parts.size == 2) { "Path must be in format 'type/slug'" }
        require(parts.all { it.isNotBlank() }) { "Path segments cannot be empty" }
    }

    private fun validatePathMatchesType() {
        val typeFromPath = path.split("/").first()
        require(typeFromPath == type.name) {
            "Path type '$typeFromPath' does not match content type '${type.name}'"
        }
    }
}
