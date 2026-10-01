fun main() {
    val n = sequence {
        for (number in 1..5) {
            yield(number)
        }
    }
    n.forEach(::println)
}