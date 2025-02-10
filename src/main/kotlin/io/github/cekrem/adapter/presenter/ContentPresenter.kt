package io.github.cekrem.adapter.presenter

import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary

interface ContentPresenter {
    fun presentContent(content: Content): Any

    fun presentContentList(contents: List<ContentSummary>): Any
}
