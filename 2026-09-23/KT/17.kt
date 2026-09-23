fun main(){
    print("Enter the Number 1:")
    val n1=readln().toInt()
    print("Enter the Number 1:")
    val n2=readln().toInt()
    val square=sq(n1,n2){n1,n2->
        n1>n2
    }
    print(square)
}
fun sq(n1:Int, n2:Int,squa:(Int,Int) -> Boolean):Boolean{
    return squa(n1,n2)
}
