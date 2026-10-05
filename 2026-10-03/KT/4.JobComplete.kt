import kotlinx.coroutines.*
fun main(){
    runBlocking{
        val lz=Job()
        launch(lz){
            println("Start of the Job")
            delay(1000)
            println("End of the Job")
        }
        lz.complete()
        lz.join()
        println("Is Job Completed: ${lz.isCompleted}")
    }
}