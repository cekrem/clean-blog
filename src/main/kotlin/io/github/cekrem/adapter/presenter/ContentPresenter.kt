package io.github.cekrem.adapter.presenter

import io.github.cekrem.adapter.dto.ContentDto
import io.github.cekrem.adapter.dto.ContentSummaryDto

interface ContentPresenter {
    fun presentContent(content: ContentDto): Any

    fun presentContentList(contents: List<ContentSummaryDto>): Any
}
