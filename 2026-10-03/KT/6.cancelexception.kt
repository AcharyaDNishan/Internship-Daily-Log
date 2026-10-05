import kotlinx.coroutines.*
fun main() = runBlocking {
    supervisorScope {
        val job = async {
            println("Coroutine started")
            throw IllegalStateException("Something went wrong")
        }
        try {
            job.await()
        } catch (e: IllegalStateException) {
            println("Caught exception: ${e.message}")
        }
        println("Is coroutine cancelled: ${job.isCancelled}")
    }
}