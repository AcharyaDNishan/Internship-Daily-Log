fun main() {
    val n = sequence {
        for (number in 1..10) {
            yield(number*number)
        }
    }
    n.forEach(::println)
}