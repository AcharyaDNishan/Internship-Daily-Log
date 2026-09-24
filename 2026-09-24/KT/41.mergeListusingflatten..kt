fun main(){
    val l=listOf(listOf(1, 2), listOf(3, 4), listOf(5, 6))
    val k=l.flatMap{it}
    println(k)
}