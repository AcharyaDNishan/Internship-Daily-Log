fun main(){
    print("Enter the number of Element to include in the List:")
    var n=readln().toInt()
    var list=mutableListOf<String>()
    for(i in 0 until n){
        print("Enter Element #${i+1}:")
        list.add(readln().uppercase())
    }
    var result=list.none{it.first()=='Z'}
    if(result){
        println("No name starts with Z.")
    } else {
        println("There is a name that starts with Z.")
    }
}
