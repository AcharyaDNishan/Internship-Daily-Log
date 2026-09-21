class Solution {
    fun findMaxConsecutiveOnes(nums: IntArray): Int {
        var cur = 0
        var max = 0
        for (n in nums) {
            if (n == 1) {
                cur++
                if (cur > max) max = cur
            } else {
                cur = 0
            }
        }
        return max
    }
}

fun main() {
    val nums = intArrayOf(1, 1, 0, 1, 1, 1)
    println("The maximum number of consecutive ones in ${nums.joinToString()} is ${Solution().findMaxConsecutiveOnes(nums)}")
}