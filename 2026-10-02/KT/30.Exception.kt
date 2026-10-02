import kotlinx.coroutines.*
fun main() {
    val job: CompletableJob = Job()
    job.invokeOnCompletion { cause ->
        if (cause == null) {
            println("Job completed successfully")
        } else {
            println("Exception received by handler: $cause")
        }
    }
    val completed = job.completeExceptionally(Exception("Failed"))
    println("completeExceptionally() returned: $completed")
    println("Is job cancelled: ${job.isCancelled}")
}