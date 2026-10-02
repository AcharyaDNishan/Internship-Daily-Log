import kotlinx.coroutines.*
import kotlin.coroutines.*
fun main(){
    runBlocking {
        val d=withTimeout(1000){
            for ( i in 1..100){
                ensureActive()
                println(i)
                delay(100)
            }
        }
    }
}