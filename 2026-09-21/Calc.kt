sealed class Calc
class Add(val a: Int, val b: Int) : Calc()
class Subtract(val a: Int, val b: Int) : Calc()
class Multiply(val a: Int, val b: Int) : Calc()
class Divide(val a: Int, val b: Int) : Calc()

fun main() {
    val operation: Calc = Add(5, 3)
    when (operation) {
        is Add -> println("Result: ${operation.a + operation.b}")
        is Subtract -> println("Result: ${operation.a - operation.b}")
        is Multiply -> println("Result: ${operation.a * operation.b}")
        is Divide -> println("Result: ${operation.a / operation.b}")
    }
}
