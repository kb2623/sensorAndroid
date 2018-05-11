@file:Suppress("unused")

package org.example.klemen.sensorandroid

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.FragmentManager
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.media.MediaRecorder
import android.net.LocalServerSocket
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import kotlinx.android.synthetic.main.frag_main.view.*
import kotlinx.android.synthetic.main.frag_recorder.*
import java.net.URL
import java.util.*

@Suppress("unused")
class FragmentPlaceholder : Fragment() {

	companion object {
		const val LOG_TAG = "FragmentPlaceholder"
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private const val ARG_SECTION_NUMBER = "section_number"

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		fun newInstance(sectionNumber: Int): FragmentPlaceholder {
			val fragment = FragmentPlaceholder()
			val args = Bundle()
			args.putInt(ARG_SECTION_NUMBER, sectionNumber)
			fragment.arguments = args
			return fragment
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val rootView = inflater.inflate(R.layout.frag_main, container, false)
		rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
		return rootView
	}
}

class FragmentTimePicker(var time_out: TextView?) : DialogFragment(), TimePickerDialog.OnTimeSetListener {

	constructor() : this(null)

	companion object {
		const val LOG_TAG = "FragmentTimePicker"
	}

	private val cal = Calendar.getInstance()

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val hour = cal.get(Calendar.HOUR_OF_DAY)
		val minute = cal.get(Calendar.MINUTE)
		val tp = TimePickerDialog(activity, this, hour, minute, true)
		return tp
	}

	override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
		val time_out_s = "%d:%d".format(hourOfDay, minute)
		Toast.makeText(activity, time_out_s, Toast.LENGTH_SHORT).show()
		time_out!!.text = time_out_s
	}

	override fun onCancel(dialog: DialogInterface?) {
		super.onCancel(dialog)
	}
}

@Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS", "PrivatePropertyName", "LocalVariableName")
class FragmentSensors : Fragment() {

	companion object {
		const val LOG_TAG = "FragmentSensors"
	}

	private var dG_acce: DataGetter? = null
	private var dG_prox: DataGetter? = null
	private var dG_gyro: DataGetter? = null
	private var dG_light: DataGetter? = null

	private var dG_acce2: DataGetter? = null
	private var dG_prox2: DataGetter? = null
	private var dG_gyro2: DataGetter? = null
	private var dG_light2: DataGetter? = null

	private var dG_acce3: DataGetter? = null
	private var dG_prox3: DataGetter? = null
	private var dG_gyro3: DataGetter? = null
	private var dG_light3: DataGetter? = null

	private lateinit var tb_prox: ToggleButton
	private lateinit var tb_acce: ToggleButton
	private lateinit var tb_gyro: ToggleButton
	private lateinit var tb_light: ToggleButton

	private lateinit var tb_prox2: ToggleButton
	private lateinit var tb_acce2: ToggleButton
	private lateinit var tb_gyro2: ToggleButton
	private lateinit var tb_light2: ToggleButton

	private lateinit var tb_prox3: ToggleButton
	private lateinit var tb_acce3: ToggleButton
	private lateinit var tb_gyro3: ToggleButton
	private lateinit var tb_light3: ToggleButton


	@SuppressLint("WrongViewCast")
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val v = inflater.inflate(R.layout.frag_sensors, container, false)

		tb_prox = v.findViewById(R.id.tb_prox)
		tb_acce = v.findViewById(R.id.tb_acce)
		tb_gyro = v.findViewById(R.id.tb_gyro)
		tb_light = v.findViewById(R.id.tb_light)

		tb_prox2 = v.findViewById(R.id.tb_prox2)
		tb_acce2 = v.findViewById(R.id.tb_acce2)
		tb_gyro2 = v.findViewById(R.id.tb_gyro2)
		tb_light2 = v.findViewById(R.id.tb_light2)

		tb_prox3 = v.findViewById(R.id.tb_prox3)
		tb_acce3 = v.findViewById(R.id.tb_acce3)
		tb_gyro3 = v.findViewById(R.id.tb_gyro3)
		tb_light3 = v.findViewById(R.id.tb_light3)

		tb_prox.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val urlText = v.findViewById<EditText>(R.id.urlText).text.toString()
			val proxRate = v.findViewById<TextView>(R.id.proxRate).text.toString().toInt()
			val urlText_prox = v.findViewById<EditText>(R.id.urlTextProx).text.toString()
			val address = "$urlText/$urlText_prox"
			if (dG_prox == null || dG_prox!!.st.address() != address) {
				val st_prox = SendTask_JSON_HTTP(URL(address), arrayOf("cm")) as SendTask<Void, Void>
				dG_prox = DataGetter(this.context!!, Sensor.TYPE_PROXIMITY, st_prox, proxRate)
			}
			if (tb_prox.isChecked) {
				dG_prox!!.start(freq)
			} else {
				dG_prox!!.stop()
			}
		})

		tb_acce.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val urlText = v.findViewById<EditText>(R.id.urlText).text.toString()
			val urlText_acce = v.findViewById<EditText>(R.id.urlTextAcce).text.toString()
			val acceRate = v.findViewById<TextView>(R.id.acceRate).text.toString().toInt()
			val address = "$urlText/$urlText_acce"
			if (dG_acce == null || dG_acce!!.st.address() != address) {
				val st_acce = SendTask_JSON_HTTP(URL(address), arrayOf("Gx", "Gy", "Gz")) as SendTask<Void, Void>
				dG_acce = DataGetter(this.context!!, Sensor.TYPE_ACCELEROMETER, st_acce, acceRate)
			}
			if (tb_acce.isChecked) {
				dG_acce!!.start(freq)
			} else {
				dG_acce!!.stop()
			}
		})

		tb_gyro.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val urlText = v.findViewById<EditText>(R.id.urlText).text.toString()
			val urlText_gyro = v.findViewById<EditText>(R.id.urlTextGyro).text.toString()
			val gyroRate = v.findViewById<TextView>(R.id.gyroRate).text.toString().toInt()
			val address = "$urlText/$urlText_gyro"
			if (dG_gyro == null || dG_gyro!!.st.address() != address) {
				val st_gyro = SendTask_JSON_HTTP(URL(address), arrayOf("x", "y", "z")) as SendTask<Void, Void>
				dG_gyro = DataGetter(this.context!!, Sensor.TYPE_GYROSCOPE, st_gyro, gyroRate)
			}
			if (tb_gyro.isChecked) {
				dG_gyro!!.start(freq)
			} else {
				dG_gyro!!.stop()
			}
		})

		tb_light.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val urlText = v.findViewById<EditText>(R.id.urlText).text.toString()
			val urlText_light = v.findViewById<EditText>(R.id.urlTextLight).text.toString()
			val lightRate = v.findViewById<TextView>(R.id.lightRate).text.toString().toInt()
			val address = "$urlText/$urlText_light"
			if (dG_acce == null || dG_acce!!.st.address() != address) {
				val st_light = SendTask_JSON_HTTP(URL(address), arrayOf("lux")) as SendTask<Void, Void>
				dG_light = DataGetter(this.context!!, Sensor.TYPE_LIGHT, st_light, lightRate)
			}
			if (tb_light.isChecked) {
				dG_light!!.start(freq)
			} else {
				dG_light!!.stop()
			}
		})

		tb_prox2.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val serverIpText = v.findViewById<EditText>(R.id.serverIpText).text.toString()
			val portText_prox = v.findViewById<EditText>(R.id.urlTextProx2).text.toString()
			val proxRate = v.findViewById<TextView>(R.id.proxRate).text.toString().toInt()
			val address = "$serverIpText:$portText_prox"
			if (dG_prox2 == null || dG_prox2!!.st.address() != address) {
				val st_prox = SendTask_Socket(address) as SendTask<Void, Void>
				dG_prox2 = DataGetter(this.context!!, Sensor.TYPE_PROXIMITY, st_prox, proxRate)
			}
			if (tb_prox2.isChecked) {
				dG_prox2!!.start(freq)
			} else {
				dG_prox2!!.stop()
			}
		})

		tb_acce2.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val serverIpText = v.findViewById<EditText>(R.id.serverIpText).text.toString()
			val portText_acce = v.findViewById<EditText>(R.id.urlTextAcce2).text.toString()
			val acceRate = v.findViewById<TextView>(R.id.acceRate).text.toString().toInt()
			val address = "$serverIpText:$portText_acce"
			if (dG_acce2 == null || dG_acce2!!.st.address() != address) {
				val st_acce = SendTask_Socket(address) as SendTask<Void, Void>
				dG_acce2 = DataGetter(this.context!!, Sensor.TYPE_ACCELEROMETER, st_acce, acceRate)
			}
			if (tb_acce2.isChecked) {
				dG_acce2!!.start(freq)
			} else {
				dG_acce2!!.stop()
			}
		})

		tb_gyro2.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val serverIpText = v.findViewById<EditText>(R.id.serverIpText).text.toString()
			val portText_gyro = v.findViewById<EditText>(R.id.urlTextGyro2).text.toString()
			val gyroRate = v.findViewById<TextView>(R.id.gyroRate).text.toString().toInt()
			val address = "$serverIpText:$portText_gyro"
			if (dG_gyro2 == null || dG_gyro2!!.st.address() != address) {
				val st_gyro = SendTask_Socket(address) as SendTask<Void, Void>
				dG_gyro2 = DataGetter(this.context!!, Sensor.TYPE_GYROSCOPE, st_gyro, gyroRate)
			}
			if (tb_gyro2.isChecked) {
				dG_gyro2!!.start(freq)
			} else {
				dG_gyro2!!.stop()
			}
		})

		tb_light2.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val serverIpText = v.findViewById<EditText>(R.id.serverIpText).text.toString()
			val portText_light = v.findViewById<EditText>(R.id.urlTextLight2).text.toString()
			val lightRate = v.findViewById<TextView>(R.id.lightRate).text.toString().toInt()
			val address = "$serverIpText:$portText_light"
			if (dG_light2 == null || dG_light2!!.st.address() != address) {
				val st_light = SendTask_Socket(address) as SendTask<Void, Void>
				dG_light2 = DataGetter(this.context!!, Sensor.TYPE_LIGHT, st_light, lightRate)
			}
			if (tb_light2.isChecked) {
				dG_light2!!.start(freq)
			} else {
				dG_light2!!.stop()
			}
		})

		tb_prox3.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val fileName = v.findViewById<TextView>(R.id.fileNameTextProx).text.toString()
			val proxRate = v.findViewById<TextView>(R.id.proxRate).text.toString().toInt()
			if (dG_prox3 == null || dG_prox3!!.st.address() != fileName) {
				val st_prox = SendTask_File(fileName, this.context!!)
				dG_prox3 = DataGetter(this.context!!, Sensor.TYPE_PROXIMITY, st_prox, proxRate)
			}
			if (tb_prox3.isChecked) {
				dG_prox3!!.start(freq)
			} else {
				dG_prox3!!.stop()

			}
		})

		tb_acce3.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val fileName = v.findViewById<TextView>(R.id.fileNameTextAcce).text.toString()
			val acceRate = v.findViewById<TextView>(R.id.gyroRate).text.toString().toInt()
			if (dG_acce3 == null || dG_acce3!!.st.address() != fileName) {
				val st_acce = SendTask_File(fileName, context)
				dG_acce3 = DataGetter(this.context!!, Sensor.TYPE_ACCELEROMETER, st_acce, acceRate)
			}
			if (tb_acce3.isChecked) {
				dG_acce3!!.start(freq)
			} else {
				dG_acce3!!.stop()
			}
		})

		tb_gyro3.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val fileName = v.findViewById<TextView>(R.id.fileNameTextGyro).text.toString()
			val gyroRate = v.findViewById<TextView>(R.id.acceRate).text.toString().toInt()
			if (dG_gyro3 == null || dG_gyro3!!.st.address() != fileName) {
				val st_gyro = SendTask_File(fileName, context)
				dG_gyro3 = DataGetter(this.context!!, Sensor.TYPE_GYROSCOPE, st_gyro, gyroRate)
			}
			if (tb_gyro3.isChecked) {
				dG_gyro3!!.start(freq)
			} else {
				dG_gyro3!!.stop()
			}
		})

		tb_light3.setOnClickListener({
			val freq = v.findViewById<EditText>(R.id.freqText).text.toString().toInt()
			val fileName = v.findViewById<TextView>(R.id.fileNameTextLight).text.toString()
			val lightRate = v.findViewById<TextView>(R.id.lightRate).text.toString().toInt()
			if (dG_light3 == null || dG_light3!!.st.address() != fileName) {
				val st_light = SendTask_File(fileName, context)
				dG_light3 = DataGetter(this.context!!, Sensor.TYPE_LIGHT, st_light, lightRate)
			}
			if (tb_light3.isChecked) {
				dG_light3!!.start(freq)
			} else {
				dG_light3!!.stop()
			}
		})

		return v
	}

}

@Suppress("CAST_NEVER_SUCCEEDS", "MemberVisibilityCanBePrivate", "PrivatePropertyName", "UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER")
class FragmentRecorder : Fragment() {

	companion object {
		const val LOG_TAG = "FragmentRecorder"
		const val REQUEST_RECORD_AUDIO_PERMISSION = 200
		const val BUFFER_SIZE = 2048
	}

	private var canRecord = false
	private var audio_data: LocalServerSocket? = null
	private var rec: Record? = null

	class Record : MediaRecorder() {

		var recording = false

		fun startRecording() {
			if (recording) return
			prepare()
			start()
			recording = true
		}

		fun stopRecording() {
			if (!recording) return
			stop()
			release()
			recording = false
		}
	}

	private lateinit var data_sampleRate: EditText
	private lateinit var data_fileName: EditText
	private lateinit var data_channels: EditText
	private lateinit var data_time: TextView
	private lateinit var btn_setTime: Button
	private lateinit var tb_record: ToggleButton

	@SuppressLint("NewApi")
	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

		val v = inflater.inflate(R.layout.frag_recorder, container, false)

		ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
		
		data_sampleRate = v.findViewById(R.id.et_sampleRate)
		data_fileName = v.findViewById(R.id.et_fileName)
		data_channels = v.findViewById(R.id.et_channels)
		data_time = v.findViewById(R.id.data_time)
		tb_record = v.findViewById(R.id.tb_record)
		btn_setTime = v.findViewById(R.id.btn_setTime)

		btn_setTime.setOnClickListener {
			Log.d(LOG_TAG, "Calling time picker dialog")
			val frag = FragmentTimePicker(data_time)
			frag.show(fragmentManager, "Time Picker")
		}

		// FIXME Popravi kodo
		tb_record.setOnClickListener({
			if (tb_record.isChecked) {
				rec!!.stopRecording()
			} else {
				audio_data = LocalServerSocket("audio_data")
				rec = Record()
				rec!!.setAudioChannels(data_channels.text as Int)
				rec!!.setAudioSamplingRate(data_sampleRate.text as Int)
				rec!!.setOutputFile(data_fileName.text as String)
				rec!!.setOutputFile(audio_data!!.fileDescriptor)
			}
		})

		return v
	}

	private fun makeRecorder(): MediaRecorder {
		val mediaRecorder = MediaRecorder()
		mediaRecorder.setAudioSamplingRate(data_sampleRate.text as Int)
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC)
		mediaRecorder.setAudioChannels(2)

		return mediaRecorder
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when(requestCode) {
			REQUEST_RECORD_AUDIO_PERMISSION -> canRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
		}
		if (!canRecord) Log.d(LOG_TAG, "CAN NOT read form audio device on your machine")
		else Log.d(LOG_TAG, "CAN read form audio device on your machine")
	}
}
