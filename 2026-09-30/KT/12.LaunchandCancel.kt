import kotlinx.coroutines.*
fun main(){
    println("Hello")
    runBlocking{
        val job=launch{
            for(i in 1 .. 10){
                println(i)
                delay(100)
            }
        }
        delay(350)
        job.cancelAndJoin()
    }
}