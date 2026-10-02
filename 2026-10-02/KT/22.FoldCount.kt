import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
    val job: Job = launch {
        print("Hello")
    }
    val c1: CoroutineContext = CoroutineName("My ")
    val c2: CoroutineContext = job
    val name: CoroutineContext =  c1 + c2 + Dispatchers.Default
    val count = name.fold(0) { acc, element -> 
        acc+1
    }
    println("Number of elements in the context: $count")
}