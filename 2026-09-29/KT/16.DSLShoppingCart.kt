class Engine(val item: String)
class PC(val cart: List<Engine>)
class PB {
    private val il=mutableListOf<Engine>()
    fun item(name: String){
        il.add(Engine(name))
    }
    fun build(): PC = PC(il)
}

fun car(b: PB.() -> Unit): PC {
    val builder = PB()
    builder.b()
    return builder.build()
}

fun main() {
    val l=car {
        item("Laptop")
        item("Mouse")
        item("Keyboard")
    }
    println(l)
}
