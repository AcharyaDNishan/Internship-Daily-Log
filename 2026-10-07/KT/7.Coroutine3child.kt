import kotlinx.coroutines.*
suspend fun CS()=coroutineScope{
    val a=launch{
        delay(2000)
        println("Child 1")
    }
    val b=launch{
        delay(1000)
        println("Child 2")
    }
    val c=launch{
        delay (3000)
        println("Child 3")
    }
    a.join()
    b.join()
    c.join()
}
fun main()=runBlocking{
    println("Operatingg...")
    CS()
    println("Done!")
}