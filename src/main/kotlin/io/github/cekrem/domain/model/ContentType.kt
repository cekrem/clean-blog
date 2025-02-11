package io.github.cekrem.domain.model

data class ContentType(
    val name: String, // e.g., "posts", "pages", "events"
    val singular: String = name.removeSuffix("s"), // e.g., "post", "page", "event"
    val listable: Boolean,
)
