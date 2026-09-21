fun leftRotate(n: IntArray, k: Int) {
    val nSize = n.size
    val actualK = k % nSize
    if (actualK == 0) {
        println("Array unchanged: ${n.joinToString()}")
        return
    }
    val result = mutableListOf<Int>()
    for (i in actualK until nSize) {
        result.add(n[i])
    }
    for (i in 0 until actualK) {
        result.add(n[i])
    }
    println("Result: ${result.joinToString()}")
}

fun main() {
    val nums = intArrayOf(3, 5, 2, 9, 5, 7)
    println("Original Array: ${nums.joinToString()}")
    println("Enter the number of left moves:")
    val k = readln().toInt()
    leftRotate(nums, k)
}