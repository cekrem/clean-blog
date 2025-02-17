package io.github.cekrem.application.config

data class BlogConfig(
    val title: String,
    val description: String,
    val url: String,
    val menuItems: List<MenuItem>,
)
