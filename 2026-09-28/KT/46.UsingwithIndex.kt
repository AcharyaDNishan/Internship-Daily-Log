fun main() {
    print("Enter the number of elements to include in the list: ")
    val n = readln().toInt()
    val list = mutableListOf<String>()
    for (i in 0 until n) {
        print("Enter element #${i + 1}: ")
        list.add(readln())
    }
    for ((index, value) in list.withIndex()) {
        println("Index $index: $value")
    }
}