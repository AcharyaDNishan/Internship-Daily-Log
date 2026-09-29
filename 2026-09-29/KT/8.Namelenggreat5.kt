fun main(){
    val numbers = listOf("apple", "banana", "cat", "dog", "apricot")
    val col=numbers
        .asSequence()
        .filter{it.count()>5}
        .take(1)
        .forEach(){println(it)}
}