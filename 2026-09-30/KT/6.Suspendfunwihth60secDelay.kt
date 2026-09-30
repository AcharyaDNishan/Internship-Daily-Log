import kotlinx.coroutines.*
suspend fun sayHello(){
     println("Hello")
}
fun main(){
    runBlocking(){
        delay(1000)
        sayHello()
    }
}