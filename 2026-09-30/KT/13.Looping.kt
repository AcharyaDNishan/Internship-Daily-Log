import kotlinx.coroutines.*
fun main(){
    val d=2
    runBlocking{
        val job=launch{
            while(d==2){
                println("Running...")
                delay(100)
            }
        }
        delay(350)
        job.cancelAndJoin()
        println("Job Cancelled. 👍")
    }
}