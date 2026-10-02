import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
    val job: Job = launch {
        print("Hello")
    }
    val c1: CoroutineContext = CoroutineName("My ")
    val c2: CoroutineContext = job
    val j=c2.minusKey(Job)
    println("New Coroutine name: ${c1.get(CoroutineName)}")
    println("Job Active: ${j.get(Job)?.isActive}")
}