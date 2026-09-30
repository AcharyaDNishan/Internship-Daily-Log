import kotlinx.coroutines.*
suspend fun first():Int{
    return 10
}
suspend fun second():Int{
    return 20
}

fun main(){
    runBlocking{
        val hello=async{first()}  
        val hell=async{second()}  
        val sum=hello.await()
        val d=hell.await()
        println(sum)
        println(d)
    } 
}