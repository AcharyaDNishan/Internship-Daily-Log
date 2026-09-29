data class NotificationData(val message: String)

class Logger {
    fun info(message: String) {
        println(message)
    }
}

data class Data(val dat: String)

class Database {
    fun saved(message: String) {
        println(message)
    }
}


context(l: Logger, db: Database)
fun sendNotification(
    n: NotificationData,d: Data
) {
    l.info("Saving notification: ${d.dat}")
    db.saved("Data has been saved.")
}

fun main() {
    val logger = Logger()
    val notification = NotificationData("Saving...")
    val db=Database()
    print("What data do you want to save in Database:")
    val d = Data(readln())
    with(logger) {
        with(db){
            sendNotification(notification,d)
        }
    }
}