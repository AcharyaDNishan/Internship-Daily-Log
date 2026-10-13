package com.example.calculation

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {
    private lateinit var operation: TextView
    private lateinit var result: TextView
    private val expression = mutableListOf<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        operation = findViewById(R.id.operation)
        result = findViewById(R.id.result)
        val numberButtons = mapOf(
            R.id.zero to "0",
            R.id.one to "1",
            R.id.two to "2",
            R.id.three to "3",
            R.id.four to "4",
            R.id.five to "5",
            R.id.six to "6",
            R.id.seven to "7",
            R.id.eight to "8",
            R.id.nine to "9",
            R.id.decimal to "."
        )
        for ((id, char) in numberButtons) {
            findViewById<Button>(id).setOnClickListener {
                expression.add(char)
                operation.text = expression.joinToString("")
            }
        }
        val opButtons = mapOf(
            R.id.plus to "+",
            R.id.minus to "-",
            R.id.multiply to "*",
            R.id.divide to "/"
        )
        for ((id, op) in opButtons) {
            findViewById<Button>(id).setOnClickListener {
                expression.add(op)
                operation.text = expression.joinToString("")
            }
        }
        findViewById<Button>(R.id.equals).setOnClickListener {
            calculateResult()
        }
    }
    private fun calculateResult() {
        if (expression.isEmpty()) return
        try {
            val numbers = mutableListOf<Double>()
            val operators = mutableListOf<String>()
            var tempStr = ""
            for (item in expression) {
                if (item == "+" || item == "-" || item == "*" || item == "/") {
                    if (tempStr.isNotEmpty()) {
                        numbers.add(tempStr.toDouble())
                        tempStr = ""
                    }
                    operators.add(item)
                } else {
                    tempStr += item
                }
            }
            if (tempStr.isNotEmpty()) {
                numbers.add(tempStr.toDouble())
            }
            if (numbers.isEmpty()) return
            var i = 0
            while (i < operators.size) {
                if (operators[i] == "*" || operators[i] == "/") {
                    val n1 = numbers[i]
                    val n2 = numbers[i + 1]
                    val res = if (operators[i] == "*") {
                        n1 * n2
                    } else {
                        if (n2 == 0.0) throw ArithmeticException("Division by zero")
                        n1 / n2
                    }
                    numbers[i] = res
                    numbers.removeAt(i + 1)
                    operators.removeAt(i)
                } else {
                    i++
                }
            }
            var resultVal = numbers[0]
            for (j in operators.indices) {
                val n2 = numbers[j + 1]
                if (operators[j] == "+") {
                    resultVal += n2
                } else if (operators[j] == "-") {
                    resultVal -= n2
                }
            }
            result.text = if (resultVal == resultVal.toInt().toDouble()) {
                resultVal.toInt().toString()
            } else {
                resultVal.toString()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid Operation", Toast.LENGTH_SHORT).show()
        }
    }
}
