class Lib(val title: String,val author: String,val price: Double)
fun main(){
    val a = Lib("IT", "Stephhen King", 9000.0)
    with(a){
        println("${title} - ${author}  : Rs.${price}")
    }
}