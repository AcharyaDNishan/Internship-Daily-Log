fun main(){
    val numbers = listOf(100, 200, 300)
    val result = numbers.fold(0) { a, b -> if(a>b) a else b}
    println(result)
}