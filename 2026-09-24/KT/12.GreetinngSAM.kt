fun interface Calculator{
    fun calculate(a: String)
}
fun main(){
    val add=Calculator{a->
        println("Hello, $a")
    }
    print("Enter you Name:")
    val n1=readln()
    add.calculate(n1)
}
