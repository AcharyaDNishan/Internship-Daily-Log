data class AppData(val Name: String, val version: Double)

class Config {
    fun info(message: String) {
        println(message)
    }
}

context(c: Config)
fun sendNotification(
    n: AppData
) {
    c.info("Name: ${n.Name}")
    c.info("Version: ${n.version}")
}

fun main() {
    val c = Config()
    print("Enter the name:")
    val n=readln()
    print("Enter Version:")
    val v=readln().toDouble()
    val ad=AppData(n,v)
    with(c) {
        sendNotification(ad)
    }
}