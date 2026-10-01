import kotlinx.coroutines.*
fun main(){
    runBlocking{
        println("Downloading file 1...")
        async{f1()}
        println("Downloading file 2...")
        async{f2()}
        println("Downloading file 3...")
        async{f3()}
    }
}
suspend fun f1(){
    delay(2600)
    println("File 1 Downloading...")
}
suspend fun f2(){
    delay(1000)
    println("File 2 Downloading...")
}
suspend fun f3(){
    delay(2000)
    println("File 3 Downloading...")
}