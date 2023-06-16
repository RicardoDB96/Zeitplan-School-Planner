package com.domberdev.zeitplan.dialog

import android.app.Dialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.TextView
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class TimePickerFragment(private val onTimeSelected: OnTimeSelected, val textView: TextView) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val minuteComplete = when(minute){
            0 -> "00"
            1 -> "01"
            2 -> "02"
            3 -> "03"
            4 -> "04"
            5 -> "05"
            6 -> "06"
            7 -> "07"
            8 -> "08"
            9 -> "09"
            else -> minute
        }
        val hour = when(hourOfDay){
            0 -> "12:$minuteComplete AM"
            1 -> "01:$minuteComplete AM"
            2 -> "02:$minuteComplete AM"
            3 -> "03:$minuteComplete AM"
            4 -> "04:$minuteComplete AM"
            5 -> "05:$minuteComplete AM"
            6 -> "06:$minuteComplete AM"
            7 -> "07:$minuteComplete AM"
            8 -> "08:$minuteComplete AM"
            9 -> "09:$minuteComplete AM"
            10 -> "10:$minuteComplete AM"
            11 -> "11:$minuteComplete AM"
            12 -> "12:$minuteComplete PM"
            13 -> "01:$minuteComplete PM"
            14 -> "02:$minuteComplete PM"
            15 -> "03:$minuteComplete PM"
            16 -> "04:$minuteComplete PM"
            17 -> "05:$minuteComplete PM"
            18 -> "06:$minuteComplete PM"
            19 -> "07:$minuteComplete PM"
            20 -> "08:$minuteComplete PM"
            21 -> "09:$minuteComplete PM"
            22 -> "10:$minuteComplete PM"
            23 -> "11:$minuteComplete PM"
            else -> "Error"
        }
        val time = when(hourOfDay){
            0 -> "00:$minuteComplete"
            1 -> "01:$minuteComplete"
            2 -> "02:$minuteComplete"
            3 -> "03:$minuteComplete"
            4 -> "04:$minuteComplete"
            5 -> "05:$minuteComplete"
            6 -> "06:$minuteComplete"
            7 -> "07:$minuteComplete"
            8 -> "08:$minuteComplete"
            9 -> "09:$minuteComplete"
            10 -> "10:$minuteComplete"
            11 -> "11:$minuteComplete"
            12 -> "12:$minuteComplete"
            13 -> "13:$minuteComplete"
            14 -> "14:$minuteComplete"
            15 -> "15:$minuteComplete"
            16 -> "16:$minuteComplete"
            17 -> "15:$minuteComplete"
            18 -> "17:$minuteComplete"
            19 -> "19:$minuteComplete"
            20 -> "20:$minuteComplete"
            21 -> "21:$minuteComplete"
            22 -> "22:$minuteComplete"
            23 -> "23:$minuteComplete"
            else -> hourOfDay.toString()
        }
        onTimeSelected.onTimeSelected(hour, time, textView)
    }

    interface OnTimeSelected{
        fun onTimeSelected(text: String, time: String, textView: TextView)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)
        return TimePickerDialog(requireContext(), this, hour, minute, false)
    }
}