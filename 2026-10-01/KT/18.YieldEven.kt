fun main() {
    val n = sequence {
        for (number in 1..10) {
            if (number % 2 == 0) yield(number)
        }
    }
    n.forEach(::println)
}