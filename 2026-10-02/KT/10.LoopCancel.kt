import kotlinx.coroutines.*
import kotlin.coroutines.*
fun main(){
    runBlocking {
        val d=launch(){
            for ( i in 1..100){
                ensureActive()
                println(i)
                delay(100)
            }
        }
        delay(500)
        d.cancelAndJoin()
    }
}