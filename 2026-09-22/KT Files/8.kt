data class Book(val title: String, val price: Double)

data class Inventory(var books: List<Book>) {
    operator fun plusAssign(book: Book) {
        books = books + book
    }
    operator fun plusAssign(other: Inventory) {
        books = books + other.books
    }
    fun totalPrice(): Double = books.sumOf { it.price }
}

fun main() {
    val b1 = Book("Kotlin", 30.0)
    val b2 = Book("Java", 20.0)
    var inventory = Inventory(emptyList())
    inventory += b1
    inventory += b2
    println(inventory.totalPrice())
    // 50.0
}