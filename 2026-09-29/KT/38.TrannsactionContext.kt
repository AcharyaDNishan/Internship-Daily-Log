class Transaction {
    fun begin() {
        println("Transaction started")
    }

    fun commit() {
        println("Transaction committed")
    }

    fun rollback() {
        println("Transaction rolled back")
    }
}

context(tx: Transaction)
fun performOperation() {
    tx.begin()
    println("Processing data inside transaction...")
    tx.commit()
}

fun main() {
    val transaction = Transaction()

    with(transaction) {
        performOperation()
    }
}
