package com.example.practice

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
suspend fun firstbird(){
    for (i in 1..4) {
        delay(1000)
        println("Coo")
    }
}
suspend fun secondbird(){
    for (i in 1..4) {
        delay(2000)
        println("Caw")
    }
}
suspend fun thirdbird(){
    for (i in 1..4) {
        delay(3000)
        println("Chirp")
    }
}