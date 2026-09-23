fun main(){
    val check=fun(n: String):String{return n.uppercase()}
    print("Enter:")
    var n=readln()
    println("Uppercase: ${check(n)}")
}