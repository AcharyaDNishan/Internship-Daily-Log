fun main(){
    val numbers = listOf(100, 200, 300)
    val result = numbers.fold(50) { a, b -> a + (b+b*0.1).toInt()}
    println(result)
}