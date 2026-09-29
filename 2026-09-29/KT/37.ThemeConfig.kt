data class AppData(val Name: String, val version: Int)

class Config {
    fun info(message: String) {
        println(message)
    }
}

context(c: Config)
fun sendNotification(
    n: AppData
) {
    c.info("Color: ${n.Name}")
    c.info("Font Size: ${n.version}")
}

fun main() {
    val c = Config()
    print("Enter the theme color: ")
    val name = readln()
    print("Enter the font size: ")
    val version = readln().toInt()
    val data = AppData(name, version)

    with(c) {
        sendNotification(data)
    }
}