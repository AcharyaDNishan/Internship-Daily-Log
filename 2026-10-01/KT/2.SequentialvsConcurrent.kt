import kotlinx.coroutines.*
suspend fun sequential(){
    delay(2000)
    print("Sequential ")
}
suspend fun concurrent(){
    delay(2000)
    print("Concurrent ")
}
suspend fun execution(){
    delay(2000)
    println("Execution")
}
fun main(){
    runBlocking{
        sequential()
        execution()
        async {concurrent()}
        async {execution()}
    }
}