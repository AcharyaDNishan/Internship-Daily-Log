import kotlinx.coroutines.*
fun main(){
    runBlocking{
        val lz=launch(start = CoroutineStart.LAZY){
            println("Start of Lazy Coroutine")
            delay(1000)
            println("End of Lazy Coroutine")
        }
        lz.start()
    }
}