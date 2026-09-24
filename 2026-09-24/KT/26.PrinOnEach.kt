fun main(){
    val list=listOf(1,2,3,4,5)
    list.onEach{n->
        println(n)
    }
    println(list)
}