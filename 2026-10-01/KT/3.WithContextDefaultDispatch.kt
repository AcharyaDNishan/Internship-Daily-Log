import kotlinx.coroutines.*
fun main(){
    print("Enter Number 1:")
    val num1=readln().toInt()
    print("Enter Number 2:")
    val num2=readln().toInt()
    runBlocking{
        val result=withContext(Dispatchers.Default){
            num1+num2
        }
        println("Result:$result")
    }
}