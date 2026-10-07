import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
fun CoroutineScope.produceNumbers() = produce<Int> {
    for (x in 1..5) send(x)
}
fun main() = runBlocking {
    val squares = produceNumbers()
    squares.consumeEach { println(it) }
    println("Done!")
}