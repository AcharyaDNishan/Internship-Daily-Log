fun main(){
    val names = listOf("Ram", "Sita", "Hari", "Anil")
    val filtered=names.filter{it.count()>3}
    println(names)
}