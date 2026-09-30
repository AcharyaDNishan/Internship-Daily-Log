import kotlinx.coroutines.*
fun main(){
    runBlocking{
        val job=launch{
            for(i in 1 until 10){
                println("Running...")
                delay(100)
            }
        }
        job.join()
        println("Job Joined. 👍")
    }
}