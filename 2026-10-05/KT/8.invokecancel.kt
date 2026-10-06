import kotlinx.coroutines.*
fun main() = runBlocking {
    val job = launch {
        println("Coroutine started")
        delay(1000)
        println("Coroutine finished")
    }
    job.invokeOnCompletion { cause ->
        if (cause is CancellationException) {
            println("Coroutine cancelled: $cause")
        }
    }
    delay(100)
    job.cancel()
    job.join()
}