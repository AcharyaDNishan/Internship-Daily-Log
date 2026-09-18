fun main(){
    val n = listOf(1,3,2,5,2,1,5,6,7)
    var n1: =listOf(n[1])
    for (i in 1..n.count()){
        for(j in 1..n1.count()){
            if(n1[j]!=n[i]){
                n1[j].add(n[i])
            } 
        }
    }
}
