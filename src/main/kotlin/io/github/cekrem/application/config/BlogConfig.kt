package io.github.cekrem.application.config

import io.github.cekrem.domain.model.MenuItem

data class BlogConfig(
    val title: String,
    val description: String,
    val url: String,
    val menuItems: List<MenuItem>,
)
