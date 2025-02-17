package io.github.cekrem.acceptance.features

import io.github.cekrem.acceptance.support.TestApplication
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

open class FeatureAcceptanceTest {
    internal val testClient = HttpClient(CIO)
    internal lateinit var testApplication: TestApplication

    @BeforeEach
    fun setUp() {
        testApplication = TestApplication.create()
        testApplication.start()
    }

    @AfterEach
    fun tearDown() {
        testClient.close()
        testApplication.stop()
    }
}
