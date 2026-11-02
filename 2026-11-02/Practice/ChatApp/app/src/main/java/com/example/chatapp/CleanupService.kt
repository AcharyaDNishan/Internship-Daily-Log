package com.example.chatapp

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class CleanupService : Service() {

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val uid = intent?.getStringExtra("user_uid")
        if (!uid.isNullOrBlank()) {
            currentUserUid = uid
        }
        return START_NOT_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        val uid = currentUserUid
        if (!uid.isNullOrBlank()) {
            Log.d("CleanupService", "App closed/task removed for user $uid. Deleting profile & chats...")
            FirebaseManager.deleteUserDataByUid(uid)
        }
        stopSelf()
    }

    companion object {
        var currentUserUid: String? = null
    }
}
