import kotlinx.coroutines.*
suspend fun greet() {
        println("Start of Lazy Coroutine")
        delay(1000)
        println("End of Lazy Coroutine")
}
fun main(){
    runBlocking{
        val lz=async(start = CoroutineStart.LAZY){
            greet()
        }
        lz.start()
        lz.await()
    }
}