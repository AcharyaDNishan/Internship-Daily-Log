fun main(){
    val numbers = listOf("Kotlin", "is", "fun")
    val result = numbers.fold("") { a, b -> a + b }
    println(result)
}