import kotlinx.coroutines.*
import kotlin.coroutines.*

suspend fun start():String {
    println("Task started!")
    return "Task completed!"
}
fun main() {
    val s:suspend()->String = suspend { start() }
    s.startCoroutine(object :Continuation<String>{
        override val context: CoroutineContext = EmptyCoroutineContext
        override fun resumeWith(result: Result<String>) {
            println("${result.getOrNull()}")
        }
    })
}