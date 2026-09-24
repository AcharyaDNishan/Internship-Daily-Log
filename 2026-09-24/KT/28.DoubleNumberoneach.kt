fun main(){
    val list=listOf(1,2,3,4,5)
    list
    .map{it*2}
    .forEach{n->
        println(n)
    }
}