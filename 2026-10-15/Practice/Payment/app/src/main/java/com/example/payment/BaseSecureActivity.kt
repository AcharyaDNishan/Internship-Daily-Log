package com.example.payment

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity

abstract class BaseSecureActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enforce FLAG_SECURE on financial screen window to prevent screen capture & spyware tapjacking
        window.setFlags(
            WindowManager.LayoutParams.FLAG_SECURE,
            WindowManager.LayoutParams.FLAG_SECURE
        )
    }
}
