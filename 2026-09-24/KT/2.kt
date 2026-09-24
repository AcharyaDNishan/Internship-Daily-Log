fun main(){
    val c={n:Int->
        n*n
    }
    print("Enter the Number:")
    val num=readln().toInt()
    println("The Square of $num is ${c(num)}.")
}