import kotlinx.coroutines.*
suspend fun first(){
    delay(200)
    println("First Operation")
}
suspend fun second(){
    delay(300)
    println("Second Operation")
}
fun main(){
    runBlocking{
        first()
        second()
    }
}