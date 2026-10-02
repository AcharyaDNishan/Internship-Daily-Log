import kotlin.concurrent.thread
import kotlin.coroutines.*
import kotlinx.coroutines.*
suspend fun waitForInput(): String = suspendCancellableCoroutine { cont ->
    println("Waiting for input...")
    val worker = thread {
        val input = readln()
        if (cont.isActive) {
            cont.resume(input)
        }
    }
    cont.invokeOnCancellation {
        worker.interrupt()
    }
}

fun main() = runBlocking {
    val s: suspend () -> String = { waitForInput() }
    println(s.invoke())
}