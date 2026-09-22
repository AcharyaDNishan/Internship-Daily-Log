class Pizza(val toppings: List<String>){
	companion object{
		fun hawaiian()=Pizza(listOf("ham","pineapple"))
    	fun margherita()=Pizza(listOf("mozzarella","tomatoes"))

	}
}
fun main(){
	val hawaiian=Pizza.hawaiian()
	println(hawaiian.toppings)
	val margherita=Pizza.margherita()
	println(margherita.toppings)
}