fun interface Calculator{
    fun calculate(a: String):String
}
fun main(){
    val add=Calculator{a->
        a.uppercase()
    }
    print("Enter:")
    val n1=readln()
    println(add.calculate(n1))
}
