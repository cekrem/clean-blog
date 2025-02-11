package io.github.cekrem.application.usecase

import io.github.cekrem.application.config.BlogConfig
import io.github.cekrem.application.config.MenuItem
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetMenuItemsUseCaseTest {
    private lateinit var blogConfig: BlogConfig
    private lateinit var getMenuItems: GetMenuItemsUseCase

    @BeforeEach
    fun setUp() {
        blogConfig = mockk()
        getMenuItems = GetMenuItemsUseCase(blogConfig)
    }

    @Test
    fun `should return menu items from blog config`() =
        runTest {
            // Given
            val expectedMenuItems =
                listOf(
                    MenuItem("Home", "/"),
                    MenuItem("Blog", "/blog"),
                    MenuItem("About", "/about"),
                )
            every { blogConfig.menuItems } returns expectedMenuItems

            // When
            val result = getMenuItems(Unit)

            // Then
            assertEquals(expectedMenuItems, result)
        }

    @Test
    fun `should return empty list when no menu items configured`() =
        runTest {
            // Given
            every { blogConfig.menuItems } returns emptyList()

            // When
            val result = getMenuItems(Unit)

            // Then
            assertEquals(emptyList(), result)
        }
}
