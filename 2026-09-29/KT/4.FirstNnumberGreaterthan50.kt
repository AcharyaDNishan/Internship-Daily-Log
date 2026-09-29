fun main(){
    val list=listOf(10, 20, 30, 60, 70, 80)
    val col=list
        .asSequence()
        .filter{it>50}
        .take(1)
        .forEach(){println(it)}
}