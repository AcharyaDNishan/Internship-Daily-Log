data class Data(val data: String)

class Database {
    fun saved(message: String) {
        println(message)
    }
}

context(db: Database)
fun save(
    d: Data
) {
    db.saved("Data '$d' has been saved.")
}

fun main() {
    val db = Database()
    print("What data do you want to save in Database:")
    val notification = Data(readln())

    with(db) {
        save(notification)
    }
}