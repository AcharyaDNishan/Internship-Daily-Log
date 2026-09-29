class PC(val Name: String)

class PB(
    var Name: String =""
){
    fun build(){ 
        println("<body>")
        PC(Name)
        println(Name)
        println("</body>")
    }
}

fun p(b: PB.()->Unit){
    val builder=PB()
    println("<html>")
    builder.b()
    builder.build()
    println("</html>")
}
fun main(){
    p{
        Name="Hello"
    }
}