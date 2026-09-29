class Computer(val Name : String, val ram : Int, val storage: Int)

class Comp(var Name : String = "", var ram : Int = 0, var storage: Int = 0){
    fun b():Computer=Computer(Name,ram,storage)
}

fun com(a: Comp.()->Unit):Computer{
    val d=Comp()
    d.a()
    return d.b()
}

fun main(){
    var m= com{
        Name="HP"
        ram=12
        storage=512
    }
    println("${m.Name} ${m.ram} ${m.storage}")
}