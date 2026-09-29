class Engine(val horsepower: Int)
class PC(val brand: String, val model: String, val engine: Engine)
class EngineBuilder {
    var horsepower: Int = 0
    fun build(): Engine = Engine(horsepower)
}
class PB {
    var brand: String = ""
    var model: String = ""
    private val engineBuilder = EngineBuilder()
    fun engine(init: EngineBuilder.() -> Unit) {
        engineBuilder.init()
    }
    fun build(): PC = PC(brand, model, engineBuilder.build())
}

fun car(b: PB.() -> Unit): PC {
    val builder = PB()
    builder.b()
    return builder.build()
}

fun main() {
    val r = car {
        brand = "Toyota"
        model = "Corolla"
        engine {
            horsepower = 120
        }
    }
    println("Car Brand: ${r.brand}")
    println("Engine Horsepower: ${r.engine.horsepower} HP")
}
