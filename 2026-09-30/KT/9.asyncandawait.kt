import kotlinx.coroutines.*
suspend fun sayHello(a:Int,b:Int):Int{
    return a+b
}
fun main(){
    println("Enter the two Numbers:")
    val n1=readln().toInt()
    val n2=readln().toInt()
    runBlocking{
        val hello=async{sayHello(n1,n2)}    
        val sum=hello.await()
        println(sum)
    } 
}