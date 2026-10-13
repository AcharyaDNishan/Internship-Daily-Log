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
            val numbers = mutableListOf<Int>()
            val operators = mutableListOf<String>()
            var temp = 0
            if("/" in operators){
                
            }
            numbers.add(temp)
            if (numbers.isNotEmpty()) {
                var opResult = numbers[0]
                for (i in operators.indices) {
                    if (i + 1 < numbers.size) {
                        when (operators[i]) {
                            "+" -> opResult += numbers[i + 1]
                            "-" -> opResult -= numbers[i + 1]
                            "*" -> opResult *= numbers[i + 1]
                            "/" -> {
                                if (numbers[i + 1] != 0) {
                                    opResult /= numbers[i + 1]
                                }
                            }
                        }
                    }
                }
                result.text = opResult.toString()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Invalid Operation", Toast.LENGTH_SHORT).show()
        }
    }
}
