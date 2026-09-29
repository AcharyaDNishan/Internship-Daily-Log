data class UserInfo(val Name: String, val Age: Int)

class User {
    fun printuserinfo(message: String) {
        println(message)
    }
}

context(c: User)
fun sendNotification(
    n: UserInfo
) {
    c.printuserinfo("Name: ${n.Name}")
    c.printuserinfo("Age: ${n.Age}")
}

fun main() {
    val c = User()
    print("Enter the name:")
    val n=readln()
    print("Enter Age:")
    val v=readln().toInt()
    val ad=UserInfo(n,v)
    with(c) {
        sendNotification(ad)
    }
}