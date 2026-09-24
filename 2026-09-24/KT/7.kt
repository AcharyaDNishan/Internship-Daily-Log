fun main(){
    val c={n1:Int,n2:Int->
        n1*n2
    }
    print("Enter the Length:")
    val num1=readln().toInt()
    print("Enter the Breadth:")
    val num2=readln().toInt()
    println("The Area of the Rectangle is ${c(num1,num2)}.")
}