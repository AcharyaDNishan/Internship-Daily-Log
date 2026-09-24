fun main(){
    val l=listOf(1, 2, 3)
    val k=l.flatMap{listOf(it,it+10)}
    println(k)
}