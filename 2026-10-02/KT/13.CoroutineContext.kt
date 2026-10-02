import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
    val job:Job=launch{
        print("Hello")
    }
    val cc: CoroutineContext = job
    val name=  cc[CoroutineName]
    println("Coroutine name: $name")
}