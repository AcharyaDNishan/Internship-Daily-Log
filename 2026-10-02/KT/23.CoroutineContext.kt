import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
    val job:Job=launch{
        print("Hello")
    }
    val cc = coroutineContext.job
    println("Coroutine Active: ${cc?.isActive}")
}