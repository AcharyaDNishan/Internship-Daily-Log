import kotlinx.coroutines.*
fun main(){
    runBlocking{
        launch{
            try{
                delay(3000)
                println("Task 1 Started...")
                throw Exception("Exception in Task 1")
            }catch (e:Exception){
                println("${e.message}")
            }
            
        }
    }
}