fun main(){
    print("Enter the number of Element to include in the List:")
    var n=readln().toInt()
    var list=mutableListOf<Int>()
    for(i in 0 until n){
        print("Enter Element #${i+1}:")
        list.add(readln().toInt())
    }
    var result=list.none{it>100}
    if(result){
        println("No number is greater than 100.")
    } else {
        println("There is a number Greater than 100.")
    }
}