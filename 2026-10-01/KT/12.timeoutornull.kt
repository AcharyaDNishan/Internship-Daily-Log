import kotlinx.coroutines.*
fun main(){
    runBlocking{
        val comp:String?=withTimeoutOrNull(2000){
            println("Task 1 Started...")
            delay(5000)
            "Task 1 Completed..."
        }
        val result=comp?: "Task 1 Timeout Exception..."
        println(result)
    }
}