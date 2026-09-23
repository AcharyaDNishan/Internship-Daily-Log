fun calculatePrice(price: Double, operation: (Double) -> Double):Double{
    return operation(price)
}
fun main(){
    print("Enter the Price:")
    val p=readln().toDouble()
    val ad=calculatePrice(p){a->
        a-a*0.1
    }
    println("After 10% discount: $ad")
}