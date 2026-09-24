fun interface Calculator{
    fun calculate(a: Double):Double
}
fun main(){
    val add=Calculator{a->
        a*9/5+32
    }
    print("Enter Number 1:")
    val n1=readln().toDouble()
    println(add.calculate(n1))
}
