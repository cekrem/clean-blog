package infrastructure.web.internal.presenter

import domain.model.Content
import domain.model.ContentSummary
import infrastructure.web.internal.template.toTemplateData
import interfaceadapters.presenter.ContentPresenter
import io.github.cekrem.infrastructure.web.internal.template.toTemplateData
import io.ktor.server.mustache.MustacheContent

class MustacheContentPresenter() : ContentPresenter {
    override fun presentContent(content: Content): MustacheContent {
        return MustacheContent("content.mustache", content.toTemplateData())
    }

    override fun presentContentList(contents: List<ContentSummary>): MustacheContent {
        return MustacheContent("list.mustache", contents.toTemplateData())
    }
}