import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
fun CoroutineScope.produceNumbers() = produce<Int> {
    for (x in 1..5) send(x)
}
fun CoroutineScope.produceNum() = produce<Int> {
    for (x in 5..10) send(x)
}
fun main() = runBlocking {
    val squares = produceNumbers()
    val cube=produceNum()
    squares.consumeEach { println(it) }
    cube.consumeEach { println(it) }
    println("Done!")
}