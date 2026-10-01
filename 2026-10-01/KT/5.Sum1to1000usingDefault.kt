import kotlinx.coroutines.*
fun main(){
    var sum=0
    runBlocking{
        val result=withContext(Dispatchers.Default){
            for (i in 1..1000){
                sum+=i
            }
        }
        println("Sum:$sum")
    }
}