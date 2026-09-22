fun pal(s: String): Boolean {
    for (i in 0 until s.count() / 2) {
        if (s[i] != s[s.count() - 1 - i]) {
            return false
        }
    }
    return true
}
fun main() {
    val s = readln()
    val r = pal(s)
    if (r) {
        println("YES")
    } else {
        println("NO")
    }
}