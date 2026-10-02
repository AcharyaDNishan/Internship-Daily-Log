import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
    val job:Job=launch{
        delay(2000)
        print("Hello")
    }
    val cc = coroutineContext.job
    cc.cancel()
}