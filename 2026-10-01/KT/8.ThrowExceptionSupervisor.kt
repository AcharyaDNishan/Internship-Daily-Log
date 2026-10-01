import kotlinx.coroutines.*
fun main(){
    runBlocking{
        supervisorScope{
            launch{
                delay(3000)
                println("Task 1")
            }
            launch{
                delay(2000)
                throw Exception("Exception in Task 2")
            }
            launch{
                delay(1000)
                println("Task 3")
            }
        }
    }
}