import kotlinx.coroutines.*
fun main(){
    runBlocking{     
        println("Starting Task 1...")
        val f1=async{f1()}
        println("Starting Task 2...")
        val f2=async{f2()}
        println("Starting Task 3...")
        val f3=async{f3()}
        print("Do you want to cancel the any task? (t1/t2/t3/n)")
        val input=readln()
        when(input){
            "t1"->{
                println("Cancelling Task 1...")
                f1.cancel()
                println("Task 1 Cancelled. Waiting for other tasks to complete...")
            }
            "t2"->{
                println("Cancelling Task 2...")
                f2.cancel()
                println("Task 2 Cancelled. Waiting for other tasks to complete...")
            }
            "t3"->{
                println("Cancelling Task 3...")
                f3.cancel()
                println("Task 3 Cancelled. Waiting for other tasks to complete...")
            }
            else->{
                println("No task is cancelled. Waiting for all tasks to complete...")
            }
        }
    }
}
suspend fun f1(){
    delay(2600)
    println("Task 1 Completed.")
}
suspend fun f2(){
    delay(1000)
    println("Task 2 Completed.")
}
suspend fun f3(){
    delay(2000)
    println("Task 3 Completed.")
}