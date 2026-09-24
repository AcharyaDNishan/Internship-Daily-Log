fun main(){
    val c={n1:Int->
        if (n1>=40) "Passed" else "Failed" 
    }
    println("Enter Your Marks:")
    val num1=readln().toInt()
    println("You have ${c(num1)}.")
}