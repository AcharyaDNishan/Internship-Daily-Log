fun main(){
    val c={n1:Int,n2:Int->
        n1+n2
    }
    println("Enter the Numbers:")
    val num1=readln().toInt()
    val num2=readln().toInt()
    println("The Sum of $num1 and $num2 is ${c(num1,num2)}.")
}