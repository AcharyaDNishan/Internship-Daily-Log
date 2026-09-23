fun main(){
    val cube=fun(n1: Int,n2: Int):Int{if(n1>n2) return n1 else return n2}
    println("Enter the 2 Numbers:")
    val n1=readln().toInt()
    val n2=readln().toInt()
    println("${cube(n1,n2)} is the Greater of the two.")
}