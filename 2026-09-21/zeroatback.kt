fun back(n: IntArray) {
    var list=n.toMutableList()
    for(i in 0 until list.size){
        for(j in 0 until list.size-1){
            if(list[j]==0){
                val temp=list[j]
                list[j]=list[j+1]
                list[j+1]=temp
            }
        }
    }
    println("The array after moving all zeros to the back is: ${list.joinToString()}")
}

fun main() {
    val nums = intArrayOf(3, 0, 2, 0, 5, 7)
    println("Original Array: ${nums.joinToString()}")
    back(nums)
}