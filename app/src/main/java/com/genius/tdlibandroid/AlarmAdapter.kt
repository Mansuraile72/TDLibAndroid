package com.genius.tdlibandroid

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.switchmaterial.SwitchMaterial
import com.genius.tdlibandroid.Alarm // ⭐ यह इम्पोर्ट शायद मिसिंग था
import com.genius.tdlibandroid.R    // ⭐ यह इम्पोर्ट भी ज़रूरी है

class AlarmAdapter(
    private val alarms: List<Alarm>,
    private val listener: OnAlarmListener
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    interface OnAlarmListener {
        fun onItemClick(alarm: Alarm)
        fun onSwitchToggle(alarm: Alarm, isEnabled: Boolean)
    }

    inner class AlarmViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeTextView: TextView = itemView.findViewById(R.id.alarmTime)
        val noteTextView: TextView = itemView.findViewById(R.id.alarmNote)
        val switch: SwitchMaterial = itemView.findViewById(R.id.alarmSwitch)
        private val clickableArea: LinearLayout = itemView.findViewById(R.id.clickable_area)

        init {
            clickableArea.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onItemClick(alarms[position])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.alarm_item, parent, false)
        return AlarmViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        holder.timeTextView.text = alarm.time
        holder.noteTextView.text = alarm.note
        holder.switch.setOnCheckedChangeListener(null)
        holder.switch.isChecked = alarm.isEnabled
        holder.switch.setOnCheckedChangeListener { _, isChecked ->
            val currentPosition = holder.adapterPosition
            if (currentPosition != RecyclerView.NO_POSITION) {
                listener.onSwitchToggle(alarms[currentPosition], isChecked)
            }
        }
    }

    override fun getItemCount() = alarms.size
}