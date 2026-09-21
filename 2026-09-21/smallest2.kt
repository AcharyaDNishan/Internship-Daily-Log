fun las(nums: IntArray){
    var l : Int = nums[0]
    var sl : Int = nums[1]
    for(i in 0 until nums.count()){
            if(nums[i]<l){
                sl=l
                l=nums[i]
            }
            else if(nums[i]<sl && nums[i]!=l){
                sl=nums[i]
            }
        }
    println("The Smallest number is $l and the second smallest number is $sl")
}

fun main() {
    val nums = intArrayOf(3, 5, 2, 9, 5, 7)
    las(nums)
}