import kotlinx.coroutines.*
fun main(){
    runBlocking{
        try{
            withTimeout(2000){
                println("Task 1 Started...")
                delay(5000)
                println("Task 1 Completed...")
            }
        }catch (e:TimeoutCancellationException){
            println("Task 1 Timeout Exception...")
        }
    }
}