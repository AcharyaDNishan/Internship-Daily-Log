
data class OrderItem(val name: String, val price: Double)
data class Order(val items: List<OrderItem>) {
    fun totalPrice(): Double = items.sumOf { it.price }
}
class OrderBuilder {
    private val itemList = mutableListOf<OrderItem>()
    fun item(name: String, price: Double = 0.0) {
        itemList.add(OrderItem(name, price))
    }
    fun build(): Order = Order(itemList)
}
fun order(init: OrderBuilder.() -> Unit): Order {
    val builder = OrderBuilder()
    builder.init()
    return builder.build()
}
fun main() {
    val myOrder = order {
        item("Pizza", price = 12.99)
        item("Coke", price = 2.50)
    }
    println(myOrder)
    println("Total Bill: $${String.format("%.2f", myOrder.totalPrice())}")
}
