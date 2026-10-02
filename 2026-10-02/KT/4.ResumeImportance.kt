import kotlin.coroutines.*
fun main() {
    val sus: suspend () -> Unit = suspend { println("Hello!") }
    val res = sus.createCoroutine(object : Continuation<Unit> {
        override val context: CoroutineContext = EmptyCoroutineContext
        override fun resumeWith(result: Result<Unit>) {
            println("Coroutine completed")
        }
    })
}