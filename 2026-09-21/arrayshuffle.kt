class Solution {
    fun shuffle(nums: IntArray, n: Int): IntArray {
        var d=mutableListOf<Int>()
        var c=nums.count()/2
        for(i in 0 until c){
            d.add(nums[i])
            d.add(nums[i+n])
        }
        return d.toIntArray()
    }
}

fun main() {
    val nums = intArrayOf(2, 5, 1, 3, 4, 7)
    val n = 3
    println("The shuffled array of ${nums.joinToString()} is ${Solution().shuffle(nums, n).joinToString()}")
}