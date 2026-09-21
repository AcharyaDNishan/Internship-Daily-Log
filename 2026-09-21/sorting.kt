fun main(){
    var nn=mutableListOf(1,2,9,1,2,5,3,7,9,1,0)
    val sorted=sort(nn)
    println("The sorted list is $sorted")
}

fun sort(n:List<Int>):List<Int>{
    var nn= n.toMutableList()
    for(i in 0 until nn.count()){
        for(j in i+1 until nn.count()){
            if(nn[i]>nn[j]){
                var temp=nn[i]
                nn[i]=nn[j]
                nn[j]=temp
            }
        }
    }
    return nn.toList()
}