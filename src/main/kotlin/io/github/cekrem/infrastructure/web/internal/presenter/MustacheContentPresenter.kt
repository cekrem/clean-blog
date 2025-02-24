package io.github.cekrem.infrastructure.web.internal.presenter

import io.github.cekrem.adapter.dto.ContentDto
import io.github.cekrem.adapter.dto.ContentSummaryDto
import io.github.cekrem.adapter.presenter.ContentPresenter
import io.ktor.server.mustache.MustacheContent

class MustacheContentPresenter : ContentPresenter {
    override fun presentContent(content: ContentDto) =
        MustacheContent(
            template = "content.mustache",
            model = content,
        )

    override fun presentContentList(contents: List<ContentSummaryDto>) =
        MustacheContent(
            template = "list.mustache",
            model = contents,
        )
}
