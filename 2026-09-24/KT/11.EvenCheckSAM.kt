fun interface Checker{
    fun check(n: Int):Boolean
}

fun main(){
    val toCheck=Checker{n->
        n%2==0
    }
    print("Enter Number 1:")
    val n1=readln().toInt()
    if(toCheck.check(n1)){
        println("Even")
    }else{
        println("Odd")
    }
}