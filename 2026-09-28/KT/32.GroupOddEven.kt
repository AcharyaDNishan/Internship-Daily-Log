fun main(){
    print("Enter the number of Element to include in the List:")
    var n=readln().toInt()
    var list=mutableListOf<Int>()
    for(i in 0 until n){
        print("Enter Element #${i+1}:")
        list.add(readln().toIntgit p())
    }
    var result=list.groupBy{it%2==0}
    println("$result")
}