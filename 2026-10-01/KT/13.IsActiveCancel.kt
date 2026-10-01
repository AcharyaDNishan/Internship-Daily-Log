import kotlinx.coroutines.*
fun main() {
    runBlocking {
        val worker = launch {
            try {
                while (isActive) {
                    println("Working...")
                    delay(500)
                }
            } finally {
                println("Worker stopped")
            }
        }
        launch {
            delay(2000)
            println("Cancelling worker...")
            worker.cancelAndJoin()
        }

        worker.join()
    }
}