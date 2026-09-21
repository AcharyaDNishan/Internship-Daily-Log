fun main() {
    val m: Double = 100.0
    val u = "Celsius"
    val fu = "Fahrenheit"
    val formula: (Double) -> Double = { temp ->
        when {
            u == "Celsius" && fu == "Fahrenheit" -> temp * 9 / 5 + 32
            u == "Celsius" && fu == "Kelvin" -> temp + 273.15
            u == "Fahrenheit" && fu == "Celsius" -> (temp - 32) * 5 / 9
            u == "Fahrenheit" && fu == "Kelvin" -> (temp - 32) * 5 / 9 + 273.15
            u == "Kelvin" && fu == "Celsius" -> temp - 273.15
            u == "Kelvin" && fu == "Fahrenheit" -> (temp - 273.15) * 9 / 5 + 32
            else -> throw IllegalArgumentException("Invalid conversion")
        }
    }

    printFinalTemperature(m, u, fu, formula)
}

fun printFinalTemperature(
    initialMeasurement: Double, 
    initialUnit: String, 
    finalUnit: String, 
    conversionFormula: (Double) -> Double
) {
    val finalMeasurement = String.format("%.2f", conversionFormula(initialMeasurement)) 
    println("$initialMeasurement degrees $initialUnit is $finalMeasurement degrees $finalUnit.")
}