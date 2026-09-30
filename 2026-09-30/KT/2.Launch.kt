import kotlinx.coroutines.*
fun main(){
    runBlocking{
        print("Hello")
        launch{
            print("Kotlin")
        }
    }
    print("from")
}