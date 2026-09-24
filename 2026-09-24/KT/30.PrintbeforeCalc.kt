fun main(){
    val list=listOf(10,25,30,34,45)
    list.onEach{n->
        println(n)
    }
    println(list.sum())
}