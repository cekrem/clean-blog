package io.github.cekrem.application.usecase

import io.github.cekrem.application.config.BlogConfig
import io.github.cekrem.domain.model.MenuItem

class GetMenuItemsUseCase(
    private val blogConfig: BlogConfig,
) : UseCase<Unit, List<MenuItem>> {
    override suspend fun invoke(input: Unit): List<MenuItem> = blogConfig.menuItems
}
