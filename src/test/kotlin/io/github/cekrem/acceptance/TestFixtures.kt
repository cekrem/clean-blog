object TestFixtures {
    fun readMarkdownPost(filename: String): String =
        javaClass.getResource("/fixtures/markdown/$filename.md")?.readText()
            ?: throw IllegalArgumentException("Markdown fixture not found: $filename")

    fun readHtmlFixture(filename: String): String =
        javaClass.getResource("/fixtures/html/$filename.html")?.readText()
            ?: throw IllegalArgumentException("HTML fixture not found: $filename")

    val blogPosts =
        listOf(
            "hello-world",
        )
}
