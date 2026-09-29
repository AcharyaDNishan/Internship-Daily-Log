fun main(){
    val name: Int? = readlnOrNull().toInt()
    name?.let{
        println(it*it)
    }
}