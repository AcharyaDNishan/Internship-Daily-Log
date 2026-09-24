fun main(){
    val c={n:Int->
        n%2==0
    }
    print("Enter the Number:")
    val num=readln().toInt()
    if(c(num)){
        println("It is Even.")
    } else {
        println("It is Odd.")
    }
}