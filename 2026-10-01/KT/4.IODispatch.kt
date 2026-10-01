import kotlinx.coroutines.*
fun main(){
    runBlocking{
        val result=withContext(Dispatchers.IO){        
            print("Enter the Data to save:")
            val num1=readln()
            println("Saving...")
            delay(5000)
            println("Data Saved Successfully!")
        }
    }
}