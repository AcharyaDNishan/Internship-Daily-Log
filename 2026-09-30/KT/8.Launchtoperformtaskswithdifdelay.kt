import kotlinx.coroutines.*
fun main(){
    runBlocking{
        launch{
            delay(1000)
            println("Task 1")
        }
        launch{
            delay(600)
            println("Task 2")
        }
        launch{
            delay(900)
            println("Task 3")
        }
    }
}