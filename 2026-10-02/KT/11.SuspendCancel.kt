import kotlinx.coroutines.*
suspend fun waitForInput(): String? = suspendCancellableCoroutine {
            print("Waiting")
            runBlocking{
            for (i in 1..5) {
                print(".")
                delay(1000)
            }
        }
}
fun main() = runBlocking {
    val result = waitForInput()
    delay(5000)
    result?.cancelAndJoin()
}