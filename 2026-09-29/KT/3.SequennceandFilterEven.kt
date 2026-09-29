fun main(){
    print("Enter the number of Elements:")
    val n=readln().toInt()
    val list=mutableListOf<Int>()
    for(i in 0 until n){
        print("Enter the Element No.${i+1}:")
        list.add(readln().toInt())
    }
    val col=list
        .asSequence()
        .filter(){it%2==0}
        .forEach(){println(it)}
}