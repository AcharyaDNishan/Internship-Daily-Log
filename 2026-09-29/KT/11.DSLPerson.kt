class PC(val Name: String, val Age: Int)

class PB(
    var Name: String ="",
    var Age: Int=0
){
    fun build(): PC=PC(Name,Age)
}

fun p(b: PB.()->Unit):PC{
    val builder=PB()
    builder.b()
    return builder.build()
}
fun main(){
    val r=p{
        Name="Nishan"
        Age=20
    }
    println(r.Name)
}