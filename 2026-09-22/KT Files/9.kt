fun linear(n: Int, array: Array<Int>): Int {
    for (i in array.indices) {
        if (array[i] == n) return i
    }
    return -1
}

fun main() {
    val num = arrayOf(1, 3, 2, 4, 7, 2, 9, 1)
    print("Enter the number you want to search in the array: ")
    val n = readlnOrNull()?.toIntOrNull()j
    if (n == null) {
        println("\nInvalid number entered.")
        return
    }
    val index = linear(n, num)
    println("\nIt is in position $index.")
}