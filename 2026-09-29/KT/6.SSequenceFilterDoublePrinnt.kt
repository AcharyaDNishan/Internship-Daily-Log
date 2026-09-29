fun main(){
    val numbers = listOf(1, 2, 3, 4, 5)
    val col=numbers
        .asSequence()
        .filter{it%2==0}
        .map(){it*2}
        .forEach(){println(it)}
}