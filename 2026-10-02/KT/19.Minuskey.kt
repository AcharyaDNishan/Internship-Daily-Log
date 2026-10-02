import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
    val c1: CoroutineContext = CoroutineName("My ")
    val name= c1.minusKey(CoroutineName)
    println("New Coroutine name: $name")
}