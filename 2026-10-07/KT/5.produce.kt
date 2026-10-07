import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
fun CoroutineScope.produceNumbers(n: Int) = produce<Int> {
    println("Producing numbers... ")
    delay(500)
    send(n)
}
fun main() = runBlocking {
    var k = 0
    var y:String="Y"
    while(y!="N"){
        val squares = produceNumbers(k)
        squares.consumeEach { println(it) }
        println("Do you want to continue?(Y/N)")
        y=readln().uppercase()
        k++
    }
    println("You chose not to continue. Exiting the program.")
    println("Done!")
}