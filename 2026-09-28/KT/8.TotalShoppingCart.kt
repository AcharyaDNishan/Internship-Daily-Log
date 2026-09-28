fun main(){
    print("Enter the number of Items you are Purchasing:")
    var n=readln().toInt()
    var list=mutableListOf<Int>()
    for(i in 0 until n){
        print("Enter the Price of Item No.${i+1}:")
        list.add(readln().toInt())
    }
    var result=list.sum()
    println("Your totals comes to Rs.$result.")
}