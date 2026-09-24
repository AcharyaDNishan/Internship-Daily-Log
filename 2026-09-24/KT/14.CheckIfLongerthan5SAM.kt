fun interface Checker{
    fun check(n: String):Boolean
}

fun main(){
    val toCheck=Checker{n->
        n.count()>=5
    }
    print("Enter String:")
    val n1=readln()
    if(toCheck.check(n1)){
        println("It does")
    }else{
        println("It doesn't")
    }
}