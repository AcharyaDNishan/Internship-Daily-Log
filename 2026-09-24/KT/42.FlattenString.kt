fun main(){
    val l=listOf("Hi", "Bye")
    val k=l.flatMap{it.toList()}
    println(k)
}