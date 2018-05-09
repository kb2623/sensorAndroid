package org.example.klemen.sensorandroid

import android.app.Dialog
import android.app.DialogFragment
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TextView
import android.widget.TimePicker
import java.util.*

class FragmentTimePicker : DialogFragment(), TimePickerDialog.OnTimeSetListener {

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		super.onCreate(savedInstanceState)
		val c = Calendar.getInstance()
		val hour = c.get(Calendar.HOUR_OF_DAY)
		val minute = c.get(Calendar.MINUTE)
		return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
	}

	override fun onTimeSet(p0: TimePicker?, p1: Int, p2: Int) {
		val tw = activity.findViewById<TextView>(R.id.tw_time)
		tw.text = "%d:%d".format(p1, p2)
	}
}