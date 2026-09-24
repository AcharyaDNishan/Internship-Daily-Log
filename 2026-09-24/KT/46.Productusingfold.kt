fun main(){
    val numbers = listOf(1, 2, 3)
    val result = numbers.fold(1) { a, b -> a * b }
    println(result)
}