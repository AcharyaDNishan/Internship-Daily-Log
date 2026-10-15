package com.example.payment

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import com.google.android.material.button.MaterialButton

class NotFoundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_404)

        findViewById<MaterialButton>(R.id.btnBackHome)?.setOnClickListener {
            val targetIntent = if (SessionManager.isAdmin) {
                Intent(this, AdminActivity::class.java)
            } else if (SessionManager.currentUser != null) {
                Intent(this, ProfileActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            startActivity(targetIntent)
            finish()
        }
    }
}
