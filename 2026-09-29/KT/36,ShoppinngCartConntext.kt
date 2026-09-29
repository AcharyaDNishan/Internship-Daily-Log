class Engine(val item: String, val price: Double)
class PB(val cart: List<Engine>)
class PC{
    fun total(cart: List<Engine>){
        println("Total: ${cart.sumOf{it.price}}")
    }
}
context(pc: PC)
fun shop(cart: PB) {
    pc.total(cart.cart)
}

fun main() {
    val v=PC()
    val l = PB(listOf(Engine("Laptop", 300.0), Engine("Mouse", 230.0), Engine("Keyboard", 260.0)))
    with(v){
        shop(l)
    }
}
