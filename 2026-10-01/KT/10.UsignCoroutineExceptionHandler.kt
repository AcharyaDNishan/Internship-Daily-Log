import kotlinx.coroutines.*
fun main(){
    runBlocking{
        val e=CoroutineExceptionHandler{c,ed->
            println("Exception Handled.")
        }
        launch(e){
            delay(3000)
            println("Task 1 Started...")
            throw Exception("Exception in Task 1")
        }
    }
}