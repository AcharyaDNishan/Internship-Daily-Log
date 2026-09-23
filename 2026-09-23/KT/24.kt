fun main(){
    val check=fun(n: Int):Boolean{if(n%2==0)return true else return false}
    print("Enter the number:")
    var n=readln().toInt()
    if(check(n)){
        println("It is Even.")
    }else{
        println("It is Odd.")
    }
    
}