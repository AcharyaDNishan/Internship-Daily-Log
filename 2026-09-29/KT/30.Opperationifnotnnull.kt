fun main(){
    var name: String? = null
    name?.let{
        name.uppercase()
        println(name)
    }
}