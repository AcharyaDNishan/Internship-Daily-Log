fun main() {
    val list = listOf(2, 1, 3, 4, 5, 2)
    val iterator = list.iterator()
    while (iterator.hasNext()) {
        println(iterator.next())
    }
}