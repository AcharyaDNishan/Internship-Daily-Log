import kotlinx.coroutines.*
suspend fun first(){
    for(i in 1 ..5){
        println(i)
    }
}
suspend fun second(){
    for(i in 1 ..5){
        println(i)
    }
}

fun main(){
    runBlocking{
        val hello=async{first()}  
        val hell=async{second()}  
        val sum=hello.join()
        val d=hell.join()
    } 
}