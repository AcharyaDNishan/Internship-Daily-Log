fun join(s1:String,s2:String,js:(String,String)->String):String{
    return js(s1,s2)
}
fun main(){
    print("Enter First String:")
    val st1=readln()
    print("Enter Second String:")
    val st2=readln()
    val st=join(st1,st2){a,b->
        a+b
    }
    println("Concatenation of $st1 and $st2 is $st")
}