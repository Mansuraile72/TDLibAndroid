package com.genius.tdlibandroid

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.widget.EditText
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.genius.tdlibandroid.presentation.HiddenTelegramActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity(), AlarmAdapter.OnAlarmListener {
    private lateinit var alarmRecyclerView: RecyclerView
    private lateinit var addAlarmFab: FloatingActionButton
    private lateinit var alarmAdapter: AlarmAdapter
    private val alarmsList = mutableListOf<Alarm>()
    private val SECRET_HOUR = 12
    private val SECRET_MINUTE = 34
    private val SECRET_CODE = "secret_code_123"

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (!isGranted) {
                Toast.makeText(this, "Permission Denied. Notifications may not work.", Toast.LENGTH_LONG).show()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        askForNotificationPermission()
        checkAndRequestExactAlarmPermission()

        alarmRecyclerView = findViewById(R.id.alarmRecyclerView)
        addAlarmFab = findViewById(R.id.addAlarmFab)
        setupRecyclerView()
        addAlarmFab.setOnClickListener {
            showAlarmDialog(null)
        }
    }

    private fun checkAndRequestExactAlarmPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            if (!alarmManager.canScheduleExactAlarms()) {
                Intent().also { intent ->
                    intent.action = Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                    startActivity(intent)
                }
            }
        }
    }

    private fun askForNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun setupRecyclerView() {
        alarmAdapter = AlarmAdapter(alarmsList, this)
        alarmRecyclerView.adapter = alarmAdapter
        alarmRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun showAlarmDialog(existingAlarm: Alarm?) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_alarm, null)
        val timePicker = dialogView.findViewById<TimePicker>(R.id.timePicker)
        val noteEditText = dialogView.findViewById<EditText>(R.id.alarmNoteEditText)
        timePicker.setIs24HourView(true)

        if (existingAlarm != null) {
            noteEditText.setText(existingAlarm.note)
            val calendar = Calendar.getInstance().apply { timeInMillis = existingAlarm.id.toLong() }
            timePicker.hour = calendar.get(Calendar.HOUR_OF_DAY)
            timePicker.minute = calendar.get(Calendar.MINUTE)
        }

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("Save", null)
            .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
            .create()

        dialog.setOnShowListener {
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setOnClickListener {
                val hour = timePicker.hour
                val minute = timePicker.minute
                val note = noteEditText.text.toString().trim()

                if (existingAlarm != null) {
                    val index = alarmsList.indexOf(existingAlarm)
                    if (index != -1) {
                        cancelAlarm(existingAlarm)
                        alarmsList.removeAt(index)
                        alarmAdapter.notifyItemRemoved(index)
                    }
                }

                if (hour == SECRET_HOUR && minute == SECRET_MINUTE && note == SECRET_CODE) {
                    val intent = Intent(this@MainActivity, HiddenTelegramActivity::class.java)
                    startActivity(intent)
                } else {
                    scheduleNormalAlarm(hour, minute, note)
                }
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun scheduleNormalAlarm(hour: Int, minute: Int, note: String) {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val newAlarm = Alarm(
            id = calendar.timeInMillis.toInt(),
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(calendar.time),
            note = note.ifBlank { "Alarm" },
            isEnabled = true
        )
        alarmsList.add(newAlarm)
        scheduleAlarm(calendar, newAlarm)
        alarmAdapter.notifyItemInserted(alarmsList.size - 1)
        Toast.makeText(this, "Alarm set!", Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("ScheduleExactAlarm")
    private fun scheduleAlarm(calendar: Calendar, alarm: Alarm) {
        val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val showIntent = PendingIntent.getActivity(
            this, alarm.id,
            Intent(this, MainActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                putExtra("FROM_ALARM", true)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val operation = PendingIntent.getBroadcast(
            this, alarm.id,
            Intent(this, AlarmReceiver::class.java).apply {
                action = "com.genius.tdlibandroid.ALARM_TRIGGER_${alarm.id}"
                putExtra("ALARM_NOTE", alarm.note)
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmClockInfo = AlarmManager.AlarmClockInfo(calendar.timeInMillis, showIntent)
        am.setAlarmClock(alarmClockInfo, operation)
    }

    private fun cancelAlarm(alarm: Alarm) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, AlarmReceiver::class.java).apply {
            action = "com.genius.tdlibandroid.ALARM_TRIGGER_${alarm.id}"
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            alarm.id,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    override fun onItemClick(alarm: Alarm) {
        showAlarmDialog(alarm)
    }

    override fun onSwitchToggle(alarm: Alarm, isEnabled: Boolean) {
        alarm.isEnabled = isEnabled
        val position = alarmsList.indexOf(alarm)
        if (isEnabled) {
            val calendar = Calendar.getInstance().apply { timeInMillis = alarm.id.toLong() }
            scheduleAlarm(calendar, alarm)
            Toast.makeText(this, "Alarm ON", Toast.LENGTH_SHORT).show()
        } else {
            cancelAlarm(alarm)
            Toast.makeText(this, "Alarm OFF", Toast.LENGTH_SHORT).show()
        }
        if (position != -1) {
            alarmAdapter.notifyItemChanged(position)
        }
    }
}