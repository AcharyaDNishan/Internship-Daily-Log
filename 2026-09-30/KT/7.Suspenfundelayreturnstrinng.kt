import kotlinx.coroutines.*
suspend fun sayHello():String{
    delay(1000)
    return "Hello"
}
fun main(){
    var hello: String
    runBlocking{
        hello=sayHello()
    } 
    println(hello)
}