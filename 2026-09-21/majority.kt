fun majority(n: Array<Int>): Int {
    var c = 0
    var maj = 0
    var nn = n.size / 2

    for (i in n.indices) {
        var ctemp = 0
        for (j in n.indices) {
            if (n[i] == n[j]) {
                ctemp++
            }
        }
        if (ctemp > nn && ctemp > c) {
            maj = n[i]
            c = ctemp
        }
    }

    if (c > nn) {
        println("The majority element is: $maj and it is present $c times")
        return maj
    } else {
        println("No majority element found")
        return -1
    }
}

fun main() {
    print("Enter the number of elements in the array: ")
    val n = readln().toInt()
    val nums = Array(n) { 0 }
    for (i in 0 until n) {
        print("Enter element $i: ")
        nums[i] = readln().toInt()
    }

    print("Original Array: ")
    println(nums.contentToString())
    majority(nums)
}