class teacher(val name: String)
class student(val name: String)
class PC(val teacher: List<teacher>, val student: List<student>)

class tb {
    var name: String = ""
    fun build(): teacher = teacher(name)
}

class sb {
    var name: String = ""
    fun build(): student = student(name)
}

class PB {
    private val tl = mutableListOf<teacher>()
    private val sl = mutableListOf<student>()
    fun teacher(init: tb.() -> Unit) {
        val builder = tb()
        builder.init()
        tl.add(builder.build())
    }
    fun student(init: sb.() -> Unit) {
        val builder = sb()
        builder.init()
        sl.add(builder.build())
    }
    fun build(): PC = PC(tl, sl)
}

fun car(b: PB.() -> Unit): PC {
    val builder = PB()
    builder.b()
    return builder.build()
}

fun main() {
    val l=car {
    teacher {
        name = "Ram"
    }

    student {
        name = "Sita"
    }
}
    println(l)
}
