fun even(a: Int,s: (Int)->Boolean):Boolean{
    return s(a)
}
fun main(){
    print("Enter the Number:")
    val n=readln().toInt()
    val even=even(n){
        n%2==0
    }
    if(even){
        println("The Number is Even.")
    } else {
        println("The Number is Odd.")
    }
}