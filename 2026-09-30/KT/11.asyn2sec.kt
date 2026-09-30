import kotlinx.coroutines.*
suspend fun first():Int{
    delay(2000)
    return 10
}
suspend fun second():Int{
    delay(2000)
    return 20
}

fun main(){
    runBlocking{
        val hello=async{first()}  
        val hell=async{second()}  
        val sum=hello.await()
        val d=hell.await()
        println(sum+d)
    } 
}