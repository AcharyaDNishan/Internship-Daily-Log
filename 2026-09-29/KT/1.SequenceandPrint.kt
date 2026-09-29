fun main(){
    val list=listOf(1, 2, 3, 4, 5)
    val col=list
        .asSequence()
        .forEach(){println(it)}
}