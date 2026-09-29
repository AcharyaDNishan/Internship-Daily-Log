fun main(){
    val list=mutableListOf<Int>()
    for(i in 0 until 1000){
        list.add(i+1)
    }
    val col=list
        .asSequence()
        .filter(){it>500 && it%7==0}
        .take(1)
        .forEach(){println(it)}
}