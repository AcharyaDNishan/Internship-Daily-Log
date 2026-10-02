import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
val cc: CoroutineContext = CoroutineName("My Coroutine")
val name=  cc.get(CoroutineName)
println("Coroutine name: $name")
}