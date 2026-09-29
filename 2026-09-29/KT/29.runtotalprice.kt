class Cart(var n: String = "", var a: Int = 0)
fun main(){
    val k=listOf(Cart("Bread",25),Cart("Milk",20))
    k.run{
        println(this.sumOf {it.a})
    }
}