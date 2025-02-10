package io.github.cekrem.infrastructure.web.internal.presenter

import io.github.cekrem.adapter.presenter.ContentPresenter
import io.github.cekrem.domain.model.Content
import io.github.cekrem.domain.model.ContentSummary
import io.github.cekrem.infrastructure.web.internal.template.toTemplateData
import io.ktor.server.mustache.MustacheContent

class MustacheContentPresenter : ContentPresenter {
    override fun presentContent(content: Content): MustacheContent =
        MustacheContent(
            "content.mustache",
            content
                .toTemplateData(),
        )

    override fun presentContentList(contents: List<ContentSummary>): MustacheContent =
        MustacheContent("list.mustache", contents.toTemplateData())
}
