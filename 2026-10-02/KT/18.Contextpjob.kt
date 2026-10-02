import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
    val job: Job = launch {
        print("Hello")
    }
    val c1: CoroutineContext = CoroutineName("My ")
    val c2: CoroutineContext = job
    val name: CoroutineContext =  c1 + c2 + Dispatchers.Default
    println("New Coroutine name: $name")
}