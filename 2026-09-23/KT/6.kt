fun square(number: Int): Int {
    return number * number
}

fun calculate(
    number: Int,
    operation: (Int) -> Int
): Int {
    return operation(number)
}

fun main() {
    val result = calculate(5, ::square)
    println(result)
}