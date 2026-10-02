import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
    val job:Job=launch{
        print("Hello")
    }
    val cc: CoroutineContext = job
    val name=  cc.get(Job)
    println("Coroutine name: ${name?.isActive}")
}