data class Book(
    val title: String,
    val author: String
) {
    companion object {
        fun novel() = Book("The Hobbit", "J.R.R. Tolkien")
        fun textbook() = Book("Kotlin Programming", "John Smith")
    }
}
fun main() {
    val novel = Book.novel()
    println(novel)
    // Book(title=The Hobbit, author=J.R.R. Tolkien)

    val textbook = Book.textbook()
    println(textbook)
    // Book(title=Kotlin Programming, author=John Smith)
}