inline fun greet(
    name: String,
    action: (String) -> Unit
) {
    action(name)
}

fun main() {
    greet("World") {
        println("Hello, $it!")
    }
}