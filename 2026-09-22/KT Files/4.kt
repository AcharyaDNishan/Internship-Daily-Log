data class Vehicle(
    val brand: String,
    val model: String,
    val type: String
){
    companion object{
        fun car()=Vehicle("Toyota", "Corolla", "Car")
        fun bike()=Vehicle("Yamaha", "R15", "Bike")
        fun truck()=Vehicle("Tata", "Prima", "Truck")
    } 
} 

fun main(){
    val car = Vehicle.car()
    println(car)
    val bike = Vehicle.bike()
    println(bike)
}