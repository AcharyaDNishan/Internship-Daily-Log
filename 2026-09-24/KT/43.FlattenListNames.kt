fun main(){
    val l=listOf(listOf("Ram", "Sita"),listOf("Hari", "Gita"))
    val k=l.flatMap{it.toList()}
    println(k)
}