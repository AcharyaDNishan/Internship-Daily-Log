import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext
fun main() = runBlocking {
    val job:Job=launch{
        launch(CoroutineName("Child1")){
            delay(3000)
            print("This is Child1")
        }
        launch(CoroutineName("Child2")){
            delay(1000)
            print("This is Child2")
        }
        launch(CoroutineName("Child3")){
            delay(2000)
            print("This is Child3")
        }
        yield()
        val children = coroutineContext.job.children.toList()
        println("Number of child jobs: ${children.size}")
    }
    job.join()
}