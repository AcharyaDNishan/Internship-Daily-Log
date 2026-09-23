fun main(){
    val numbers = listOf(1, 2, 3, 4, 5, 6, 7, 8)
    val cal=numbers.filter{it%2==0}
    val fin=cal.map{it*10}
    println(fin)
}