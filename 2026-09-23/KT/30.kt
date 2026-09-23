fun main(){
    val cube=fun(n1: String):Int{return n1.count()}
    print("Enter your name:")
    val n1=readln()
    println("$n1 has ${cube(n1)} letters.")
}