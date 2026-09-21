class Solution {
    fun getConcatenation(nums: IntArray): IntArray {
        var ans=mutableListOf<Int>()
        for(i in 0 until nums.count()){
            ans.add(nums[i])
        }
        for(i in 0 until nums.count()){
            ans.add(nums[i])
        }
        return ans.toIntArray()
    }
}
fun main() {
    val nums = intArrayOf(1, 2, 3)
    println("The concatenation of ${nums.joinToString()} is ${Solution().getConcatenation(nums).joinToString()}")
}