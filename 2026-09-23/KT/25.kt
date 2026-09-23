fun main(){
    val check=fun(n: Int):Boolean{return (n>=18)}
    print("Enter your Age:")
    var n=readln().toInt()
    if(check(n)){
        println("You are an Adult.")
    }else{
        println("You are a Minor.")
    }
    
}