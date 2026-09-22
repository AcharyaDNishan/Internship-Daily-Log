data class Account(
    val balance: Double
)
{
    operator fun plus(amount: Double): Account = Account(balance + amount)
    operator fun minus(amount: Double): Account = Account(if(balance>amount)balance - amount else amount-balance)
}
fun main() {
    val account = Account(1000.0)
    println(account + 500.0)
    println(account - 200.0)
}