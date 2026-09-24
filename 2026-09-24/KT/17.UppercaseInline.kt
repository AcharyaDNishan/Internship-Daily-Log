inline fun uc(s:String,case:(String)->String):String{
    return case(s)
}
fun main() {
    print("Enter the Name:")
    var n=readln()
    val upper = uc(n) { a ->
        a.uppercase()
    }
    println(upper)
}