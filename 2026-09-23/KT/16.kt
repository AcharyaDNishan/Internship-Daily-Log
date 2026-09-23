fun main(){
    print("Enter the Number:")
    val n=readln()
    val square=sq(n.toInt()){n->
        n*n
    }
    print(square)
}
fun sq(n:Int,squa:(Int) -> Int):Int{
    return squa(n)
}