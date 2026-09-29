data class NotificationData(val message: String)

class Logger {
    fun info(message: String) {
        println(message)
    }
}

context(l: Logger)
fun sendNotification(
    n: NotificationData
) {
    logger.info("Sending notification ${n.message}")
}

fun main() {
    val logger = Logger()
    val notification = NotificationData("Saving...")
    with(logger) {
        sendNotification(notification)
    }
}