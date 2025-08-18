package com.genius.tdlibandroid

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat

class AlarmService : Service() {
    private var ringtone: Ringtone? = null
    private val CHANNEL_ID = "alarm_channel"

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent?.action == "DISMISS") {
            Log.d("AlarmService", "Dismiss action received. Stopping service.")
            stopSelf()
            return START_NOT_STICKY
        }

        if (ringtone != null && ringtone!!.isPlaying) {
            Log.d("AlarmService", "Alarm is already playing. Ignoring new start command.")
            return START_STICKY
        }

        Log.d("AlarmService", "Starting alarm service and playing ringtone.")
        val note = intent?.getStringExtra("ALARM_NOTE") ?: "Time to wake up!"

        createChannelIfNeeded()
        val notification = buildNotification(note)
        startForeground(1001, notification)

        playAlarmSound()

        return START_STICKY
    }

    private fun playAlarmSound() {
        try {
            val alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            if (alarmSoundUri != null) {
                ringtone = RingtoneManager.getRingtone(applicationContext, alarmSoundUri)
                ringtone?.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun buildNotification(note: String): Notification {
        val dismissIntent = Intent(this, AlarmService::class.java).apply { action = "DISMISS" }
        val dismissPendingIntent = PendingIntent.getService(this, 1, dismissIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        val fullScreenIntent = Intent(this, MainActivity::class.java)
        val fullScreenPendingIntent = PendingIntent.getActivity(this, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Alarm!")
            .setContentText(note)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setOngoing(true)
            .setFullScreenIntent(fullScreenPendingIntent, true)
            .addAction(android.R.drawable.ic_menu_close_clear_cancel, "Dismiss", dismissPendingIntent)
            .build()
    }

    private fun createChannelIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Alarm Channel", NotificationManager.IMPORTANCE_HIGH)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AlarmService", "Service destroyed. Stopping ringtone.")
        ringtone?.stop()
        ringtone = null
    }
}