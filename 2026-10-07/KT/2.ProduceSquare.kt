import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
fun CoroutineScope.produceNumbers() = produce<Int> {
    for (x in 1..10) send(x*x)
}
fun main() = runBlocking {
    println("Enter the number of squares you want to produce:")
    val n=readln()
    println("You entered: $n. But the program will produce squares of numbers from 1 to 10. Because the program is designed to produce squares of numbers from 1 to 10, regardless of the input. And I don't know how to send the input to the produce function.")
    val squares = produceNumbers()
    squares.consumeEach { println(it) }
    println("Done!")
}