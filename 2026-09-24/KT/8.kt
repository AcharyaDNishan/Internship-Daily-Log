fun main(){
    val c={n1:String->
        n1.first()=='A'
    }
    print("Enter the String:")
    val num1=readln().uppercase()
    if(c(num1)){
        println("It Begins with an A.")
    } else {
        println("It doesn't begin with an A.")
    }
}