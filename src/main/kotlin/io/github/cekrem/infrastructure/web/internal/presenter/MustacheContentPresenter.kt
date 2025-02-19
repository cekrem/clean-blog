package io.github.cekrem.infrastructure.web.internal.presenter

import io.github.cekrem.adapter.dto.dto
import io.github.cekrem.adapter.presenter.ContentPresenter
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary
import io.ktor.server.mustache.MustacheContent

class MustacheContentPresenter : ContentPresenter {
    override fun presentContent(content: Content) =
        MustacheContent(
            "content.mustache",
            content
                .dto(),
        )

    override fun presentContentList(contents: List<ContentSummary>) = MustacheContent("list.mustache", contents.dto())
}
