class Person(var n: String = "", var a: Int = 0)
fun main(){
    val k=Person().apply{
        n="Nishan"
        a=20
    }
    k.also{
        println("${it.n} Person has been created.")
    }
}