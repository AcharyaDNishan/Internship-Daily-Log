import java.math.BigDecimal
data class Money(val amount: BigDecimal, val currency: Currency) {
    operator fun plus(other: Money): Money = Money(amount + other.amount, currency)
    operator fun minus(other: Money): Money = Money(amount - other.amount, currency)
    operator fun unaryMinus(): Money = Money(-amount, currency)
    operator fun times(multiplier: Int): Money = Money(amount * multiplier.toBigDecimal(), currency)
    companion object {
        fun eur(amount: String): Money = Money(BigDecimal(amount), Currency.EUR)
    }
}
enum class Currency { EUR, USD, GBP }
fun main() {
    val money1 = Money.eur("10.00")
    val money2 = Money.eur("29.99")
    println(money1 + money2)
    println(money1 - money2)
    println(-money1)
    println(money1 * 3)
}
