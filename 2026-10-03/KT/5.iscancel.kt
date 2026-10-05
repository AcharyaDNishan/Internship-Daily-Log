import kotlinx.coroutines.*
suspend fun greet() {
        println("Start of Lazy Coroutine")
        delay(2000)
        println("End of Lazy Coroutine")
}
fun main(){
    runBlocking{
        val lz=async(start = CoroutineStart.LAZY){
            greet()
        }
        lz.start()
        delay(1000)
        lz.cancel()
        println("Is Coroutine Cancelled: ${lz.isCancelled}")
    }
}