import kotlinx.coroutines.*
fun main(){
    runBlocking{
        println("A")
    }
    runBlocking{
        delay(1000)
        println("B")
    }
}