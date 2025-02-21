package io.github.cekrem.acceptance

object TestFixtures {
    data class Fixture(
        val markdownInput: String,
        val expectedHtmlOutput: String,
    )

    private fun readMarkdownFile(filename: String): String =
        javaClass.getResource("/fixtures/markdown/$filename.md")?.readText()
            ?: throw IllegalArgumentException("Markdown fixture not found: $filename")

    private fun readHtmlFile(filename: String): String =
        javaClass.getResource("/fixtures/html/$filename.html")?.readText()
            ?: throw IllegalArgumentException("HTML fixture not found: $filename")

    val blogPosts =
        listOf(
            "usecases",
        ).associateWith {
            Fixture(
                markdownInput = readMarkdownFile(it),
                expectedHtmlOutput = readHtmlFile(it),
            )
        }
}
