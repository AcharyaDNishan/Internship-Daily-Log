fun main(){
    val cube=fun(n: Int):Int{return n*n*n}
    print("Enter the Number:")
    val n=readln().toInt()
    println("The cube of $n is ${cube(n)}.")
}