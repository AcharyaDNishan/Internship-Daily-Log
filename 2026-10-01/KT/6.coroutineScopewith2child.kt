import kotlinx.coroutines.*
fun main(){
    runBlocking{    
        coroutineScope{
            launch{
                delay(2000)
                println("Task 1")
            }
            launch{
                delay(1000)
                println("Task 2")
            }
        }
    }
}