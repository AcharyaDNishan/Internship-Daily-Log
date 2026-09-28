fun main(){
    print("Enter the number of Students:")
    var n=readln().toInt()
    var list=mutableListOf<Int>()
    for(i in 0 until n){
        print("Enter the Marks of Student No.${i+1}:")
        list.add(readln().toInt())
    }
    var result=list.sum()
    println("Your class has scored a total of $result Marks.")
}