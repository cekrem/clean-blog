package interfaceadapters.presenter

import domain.model.Content
import domain.model.ContentSummary

interface ContentPresenter {
    fun presentContent(content: Content): Any
    fun presentContentList(contents: List<ContentSummary>): Any
}