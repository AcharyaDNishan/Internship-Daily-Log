package com.example.practice

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*
import kotlin.coroutines.*
public val b=0
suspend fun firstbird(){
    while(b==0){
        val cor: CoroutineContext= CoroutineName("firstbird")
        delay(1000)
        println("${cor.get(CoroutineName)}=Coo")
    }
}
suspend fun secondbird(){
    while(b==0){
        val cor: CoroutineContext= CoroutineName("secondbad")
        delay(2000)
        println("${cor.get(CoroutineName)}=Caw")
    }
}
suspend fun thirdbird(){
    while(b==0){
        val cor: CoroutineContext= CoroutineName("thirdbird")
        delay(3000)
        println("${cor.get(CoroutineName)}=Chirp")
    }
}