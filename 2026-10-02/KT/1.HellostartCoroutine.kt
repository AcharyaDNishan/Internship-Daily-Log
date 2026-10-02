import kotlinx.coroutines.*
import kotlin.coroutines.*

suspend fun greet(): String {
    println("Hello")
    return "World"
}

fun main() {
    val sus: suspend () -> String = suspend { greet() }
    sus.startCoroutine(object : Continuation<String> {
        override val context: CoroutineContext = EmptyCoroutineContext
        override fun resumeWith(result: Result<String>) {
            println("${result.getOrNull()}")
        }
    })
}