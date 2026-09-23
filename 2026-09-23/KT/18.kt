fun main(){
    println("What is your name?")
    greeting(readln())
}
val greeting:(String)->Unit={name->
    println("Hello $name, Welcome.")
}