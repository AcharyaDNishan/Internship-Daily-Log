class Solution {
    fun reverse(x: Int): Int {
        var xy = x
        var a:Int
        var b:Int=0
        while(xy!=0){
            a=xy%10
            b=b*10+a
            xy=xy/10
        }
    return b
    }
}

fun main(){
    println("The reverse of 123 is ${Solution().reverse(123)}")
}