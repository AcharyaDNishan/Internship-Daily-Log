class Person(val n: String, val a: Int)
fun main(){
    val nn=Person("Nishan",20)
    val k=nn.run{
        "$n"
    }
    println(k)
}