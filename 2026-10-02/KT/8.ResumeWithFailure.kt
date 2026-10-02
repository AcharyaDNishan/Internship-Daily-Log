import kotlin.coroutines.*
suspend fun manualResume(): String = suspendCoroutine { continuation ->
    println("Waiting for manual resume...")
    Thread {
        Thread.sleep(1000)
        continuation.resumeWith(Result.failure(Exception("Manual Resume Failed 😞 ")))
    }.start()
}
fun main() {
    val sus: suspend () -> String = ::manualResume
    val res = sus.createCoroutine(object : Continuation<String> {
        override val context: CoroutineContext = EmptyCoroutineContext
        override fun resumeWith(result: Result<String>) {
            println("${result.exceptionOrNull()}")
        }
    })
    res.resume(Unit)
}