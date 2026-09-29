class Computer(val Name : String, val age : Int, val course: String)

class Comp(var Name : String = "", var age : Int = 0, var course: String = ""){
    fun b():Computer=Computer(Name,age,course)
}

fun com(a: Comp.()->Unit):Computer{
    val d=Comp()
    d.a()
    return d.b()
}

fun main(){
    var m= com{
        Name="Nishan"
        age=20
        course="CSE"
    }
    println("${m.Name} ${m.age} ${m.course}")
}