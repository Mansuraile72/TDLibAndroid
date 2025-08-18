package com.genius.tdlibandroid

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import androidx.core.content.ContextCompat

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val note = intent.getStringExtra("ALARM_NOTE") ?: "Time to wake up!"
        val pendingResult = goAsync()

        val serviceIntent = Intent(context, AlarmService::class.java).apply {
            putExtra("ALARM_NOTE", note)
        }
        ContextCompat.startForegroundService(context, serviceIntent)

        pendingResult.finish()
    }
}