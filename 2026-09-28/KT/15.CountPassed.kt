fun main(){
    print("Enter the number of Element to include in the List:")
    var n=readln().toInt()
    var list=mutableListOf<Int>()
    for(i in 0 until n){
        print("Enter Element #${i+1}:")
        list.add(readln().toInt())
    }
    var result=list
        .filter{it>=40}
        .count()
    println("Count: $result")
}