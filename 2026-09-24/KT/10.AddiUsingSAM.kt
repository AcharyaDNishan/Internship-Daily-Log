fun interface Calculator{
    fun calculate(a: Int, b: Int):Int
}
fun main(){
    val add=Calculator{a,b->
        a+b
    }
    print("Enter Number 1:")
    val n1=readln().toInt()
    print("Enter Number 2:")
    val n2=readln().toInt()
    println(add.calculate(n1,n2))
}
