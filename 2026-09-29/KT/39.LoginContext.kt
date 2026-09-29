class Mode(var Mode: String)
class Login {
    fun Development() {
        println("Development Mode Activated")
    }
    fun Production() {
        println("Production Mode Activated")
    }
}

context(l: Login)
fun performOperation(d:Mode) {
    val dev=Mode("Development")
    val prod=Mode("Production")
    if(d==dev){
        l.Development()
    }else if (d==prod)
    {
        l.Production()
    }else{
        println("Invalid User.")
    }
}

fun main() {
    val transaction = Login()
    print("User Type:")
    val p=Mode(readln())
    with(transaction) {
        performOperation(p)
    }
}
