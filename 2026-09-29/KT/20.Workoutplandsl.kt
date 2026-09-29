data class teacher(val name: String, val repetitions: Int)
data class PC(val teacher: List<teacher>)

class tb {
    var name: String = ""
    var repetitions: Int = 0
    fun build(): teacher = teacher(name, repetitions)
}

class PB {
    private val tl = mutableListOf<teacher>()

    fun exercise(name: String, init: tb.() -> Unit) {
        val builder = tb()
        builder.name = name
        builder.init()
        tl.add(builder.build())
    }

    fun build(): PC = PC(tl)
}

fun car(b: PB.() -> Unit): PC {
    val builder = PB()
    builder.b()
    return builder.build()
}

fun workout(b: PB.() -> Unit): PC = car(b)

fun main() {
    val l=workout {
        exercise("Push-ups") {
            repetitions = 20
        }

        exercise("Squats") {
            repetitions = 30
        }
    }
    println(l)
}
