fun main(){
    print("Enter the number of Element to include in the List:")
    var n=readln().toInt()
    var list=mutableListOf<Int>()
    for(i in 0 until n){
        print("Enter Element #${i+1}:")
        list.add(readln().toInt())
    }
    var result=list.fold(list.first()){a,e->if (a<e) a else e}
    println("Smallest: $result")
}