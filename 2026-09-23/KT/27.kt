fun main(){
    val cube=fun(n1: Int,n2: Int):Int{return n1+n2}
    println("Enter the 2 Numbers:")
    val n1=readln().toInt()
    val n2=readln().toInt()
    println("The Sum of $n1 and $n2 is ${cube(n1,n2)}.")
}