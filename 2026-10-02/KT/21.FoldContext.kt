import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
    val c1: CoroutineContext = CoroutineName("My ")
    val c2: CoroutineContext = CoroutineName("Coroutine")
    val name=  c1 + c2
    name.fold(Unit) { _, element -> 
        println(element)
    }
}