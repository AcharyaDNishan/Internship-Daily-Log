fun calculate(
    a: Int,
    b: Int,
    operation: (Int, Int) -> Int
): Int {
    return operation(a, b)
}

fun main() {
    val addition = calculate(5, 10) { a, b ->
        a + b
    }
    val multiplication = calculate(5, 10) { a, b ->
        a * b
    }
    println("Addition: $addition")
    println("Multiplication: $multiplication")
}