fun main() {
    val n = sequence {
        for (number in 1..10) {
            if(prime(number)==true) yield(number)
        }
    }
    n.forEach(::println)
}
fun prime(n:Int):Boolean{
    for(i in 2..n/2){
        if(n%i==0) return false
    }
    return true
}