class Grade(val name: String,val marks: Double)
fun main(){
    val list=listOf(Grade("Ram",40.01),Grade("Shyam",39.99),Grade("Hari",40.0))
    println(list.filter{it.marks>=40})
}