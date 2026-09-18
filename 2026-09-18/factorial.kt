fun re(n: Int): Int {
    if (n > 1) {
        return n * re(n - 1)
    } else {
        return 1
    }
}

fun main() {
    print(re(5))
}
