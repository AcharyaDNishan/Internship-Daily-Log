fun main(){
    print("Enter the number of Element to include in the List:")
    var n=readln().toInt()
    var list=mutableListOf<String>()
    for(i in 0 until n){
        print("Enter Element #${i+1}:")
        list.add(readln())
    }
    var result=list.sumOf{it.count()}
    println("$result")
}