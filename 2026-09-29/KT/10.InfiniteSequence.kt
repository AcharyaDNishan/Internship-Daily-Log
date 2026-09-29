fun main(){
    val inf=generateSequence(1) { it + 1 } 
    val col=inf
        .take(5)  
        .forEach(){print(it)}
}