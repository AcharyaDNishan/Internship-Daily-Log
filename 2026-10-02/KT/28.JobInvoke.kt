import kotlinx.coroutines.*
fun main() {
    val job: CompletableJob = Job()
    job.invokeOnCompletion { cause ->
        if (cause == null) {
            println("Job completed successfully")
        } else {
            println("Job failed: ${cause.message}")
        }
    }
    val completed = job.complete()
    println("complete() returned: $completed")
    println("Is job completed: ${job.isCompleted}")
}