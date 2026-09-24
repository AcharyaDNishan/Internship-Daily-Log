inline fun process(numbers: List<Int>, operation: (Int) -> Int): List<Int> {
    return numbers.map(operation)
}

fun main() {
    val numbers = listOf(1, 2, 3, 4, 5)
    val result = process(numbers) { it * 5 }
    println(result)
}