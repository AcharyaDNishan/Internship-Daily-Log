import kotlinx.coroutines.*
fun main(){
    runBlocking{
        println("1")
        launch{
            for(i in 2..4){
                println(i)
            }
        }
    }
    println("5")
}