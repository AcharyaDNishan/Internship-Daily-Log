package com.example.practice
import android.content.*

class AirplaneMode: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if(intent?.action == Intent.ACTION_AIRPLANE_MODE_CHANGED) {
            println("Airport mode")
        }
    }
}