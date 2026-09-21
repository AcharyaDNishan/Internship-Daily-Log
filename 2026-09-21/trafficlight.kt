sealed class TrafficLight {
    object Red : TrafficLight()
    object Yellow : TrafficLight()
    object Green : TrafficLight()
}

fun handleLight(light: TrafficLight) {
    when (light) {
        is TrafficLight.Red -> println("Stop!")
        is TrafficLight.Yellow -> println("Get ready...")
        is TrafficLight.Green -> println("Go!")
    }
}

fun main() {
    val currentLight: TrafficLight = TrafficLight.Red
    handleLight(currentLight)
}