fun main(){
    val l=listOf(1,2,3,4,5)
    val k=l.fold(0){a,b->
        if (prime(b)<=2) a+b else a
    }
    println(k)
}
fun prime(b: Int):Int{
    var c: Int=0
    for (i in 1 .. b){
            if (b%i==0){
                c++
            }
        }
    return c
}