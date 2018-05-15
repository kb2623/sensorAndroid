@file:Suppress("unused", "DEPRECATION", "CAST_NEVER_SUCCEEDS", "MemberVisibilityCanBePrivate", "PrivatePropertyName", "UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER", "UNCHECKED_CAST")

package org.example.klemen.sensorandroid

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.media.MediaRecorder
import android.net.LocalServerSocket
import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.instacart.library.truetime.TrueTimeRx
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.frag_main.view.*
import kotlinx.android.synthetic.main.frag_recorder.*
import kotlinx.android.synthetic.main.frag_recorder.view.*
import kotlinx.android.synthetic.main.frag_sensors.view.*
import kotlinx.android.synthetic.main.frag_time.view.*
import java.net.URL
import java.util.*

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

class FragmentTimePicker() : DialogFragment(), TimePickerDialog.OnTimeSetListener {

	private var time_out: TextView? = null

	constructor(time_out: TextView) : this() {
		this.time_out = time_out
	}

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
		val time_out_s = "%2d:%2d".format(hourOfDay, minute)
		Toast.makeText(activity, time_out_s, Toast.LENGTH_SHORT).show()
		time_out!!.text = time_out_s
	}

	override fun onCancel(dialog: DialogInterface?) {
		super.onCancel(dialog)
	}
}

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

	private lateinit var uic: View

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val uic = inflater.inflate(R.layout.frag_sensors, container, false)
		uic.fsenTbtnProxUrlSend.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val urlText = uic.fsenEtUrl.text.toString()
			val proxRate = uic.fsenEtProxRate.text.toString().toInt()
			val urlText_prox = uic.fsenEtProxUrl.text.toString()
			val address = "$urlText/$urlText_prox"
			if (dG_prox == null || dG_prox!!.st.address() != address) {
				val st_prox = SendTask_JSON_HTTP(URL(address), arrayOf("cm")) as SendTask<Void, Void>
				dG_prox = DataGetter(this.context!!, Sensor.TYPE_PROXIMITY, st_prox, proxRate)
			}
			startStopDataGetter(freq, dG_prox!!, uic.fsenTbtnProxUrlSend)
		}
		uic.fsenTbtnAcceUrlSend.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val urlText = uic.fsenEtUrl.text.toString()
			val urlText_acce = uic.fsenEtAcceUrl.text.toString()
			val acceRate = uic.fsenEtAcceRate.text.toString().toInt()
			val address = "$urlText/$urlText_acce"
			if (dG_acce == null || dG_acce!!.st.address() != address) {
				val st_acce = SendTask_JSON_HTTP(URL(address), arrayOf("Gx", "Gy", "Gz")) as SendTask<Void, Void>
				dG_acce = DataGetter(this.context!!, Sensor.TYPE_ACCELEROMETER, st_acce, acceRate)
			}
			startStopDataGetter(freq, dG_acce!!, uic.fsenTbtnAcceUrlSend)
		}
		uic.fsenTbtnGyroUrlSend.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val urlText = uic.fsenEtUrl.text.toString()
			val urlText_gyro = uic.fsenEtGyroUrl.text.toString()
			val gyroRate = uic.fsenEtGyroRate.text.toString().toInt()
			val address = "$urlText/$urlText_gyro"
			if (dG_gyro == null || dG_gyro!!.st.address() != address) {
				val st_gyro = SendTask_JSON_HTTP(URL(address), arrayOf("x", "y", "z")) as SendTask<Void, Void>
				dG_gyro = DataGetter(this.context!!, Sensor.TYPE_GYROSCOPE, st_gyro, gyroRate)
			}
			startStopDataGetter(freq, dG_gyro!!, uic.fsenTbtnGyroUrlSend)
		}
		uic.fsenTbtnLightUrlSend.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val urlText = uic.fsenEtUrl.text.toString()
			val urlText_light = uic.fsenEtLightUrl.text.toString()
			val lightRate = uic.fsenEtLightRate.text.toString().toInt()
			val address = "$urlText/$urlText_light"
			if (dG_acce == null || dG_acce!!.st.address() != address) {
				val st_light = SendTask_JSON_HTTP(URL(address), arrayOf("lux")) as SendTask<Void, Void>
				dG_light = DataGetter(this.context!!, Sensor.TYPE_LIGHT, st_light, lightRate)
			}
			startStopDataGetter(freq, dG_light!!, uic.fsenTbtnLightUrlSend)
		}
		uic.fsenTbtnProxPortSend.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val serverIpText = uic.fsenEtServerIp.text.toString()
			val portText_prox = uic.fsenEtProxPort.text.toString()
			val proxRate = uic.fsenEtProxRate.text.toString().toInt()
			val address = "$serverIpText:$portText_prox"
			if (dG_prox2 == null || dG_prox2!!.st.address() != address) {
				val st_prox = SendTask_Socket(address) as SendTask<Void, Void>
				dG_prox2 = DataGetter(this.context!!, Sensor.TYPE_PROXIMITY, st_prox, proxRate)
			}
			startStopDataGetter(freq, dG_prox2!!, uic.fsenTbtnProxPortSend)
		}
		uic.fsenTbtnAccePortSend.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val serverIpText = uic.fsenEtServerIp.text.toString()
			val portText_acce = uic.fsenEtAccePort.text.toString()
			val acceRate = uic.fsenEtAcceRate.text.toString().toInt()
			val address = "$serverIpText:$portText_acce"
			if (dG_acce2 == null || dG_acce2!!.st.address() != address) {
				val st_acce = SendTask_Socket(address) as SendTask<Void, Void>
				dG_acce2 = DataGetter(this.context!!, Sensor.TYPE_ACCELEROMETER, st_acce, acceRate)
			}
			startStopDataGetter(freq, dG_acce2!!, uic.fsenTbtnAccePortSend)
		}
		uic.fsenTbtnGyroPortSend.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val serverIpText = uic.fsenEtServerIp.text.toString()
			val portText_gyro = uic.fsenEtGyroPort.text.toString()
			val gyroRate = uic.fsenEtGyroRate.text.toString().toInt()
			val address = "$serverIpText:$portText_gyro"
			if (dG_gyro2 == null || dG_gyro2!!.st.address() != address) {
				val st_gyro = SendTask_Socket(address) as SendTask<Void, Void>
				dG_gyro2 = DataGetter(this.context!!, Sensor.TYPE_GYROSCOPE, st_gyro, gyroRate)
			}
			startStopDataGetter(freq, dG_gyro2!!, uic.fsenTbtnGyroPortSend)
		}
		uic.fsenTbtnLightPortSend.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val serverIpText = uic.fsenEtServerIp.text.toString()
			val portText_light = uic.fsenEtLightPort.text.toString()
			val lightRate = uic.fsenEtLightRate.text.toString().toInt()
			val address = "$serverIpText:$portText_light"
			if (dG_light2 == null || dG_light2!!.st.address() != address) {
				val st_light = SendTask_Socket(address) as SendTask<Void, Void>
				dG_light2 = DataGetter(this.context!!, Sensor.TYPE_LIGHT, st_light, lightRate)
			}
			startStopDataGetter(freq, dG_light2!!, uic.fsenTbtnGyroPortSend)
		}
		uic.fsenTbtnProxToFile.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val fileName = uic.fsenEtProxFile.text.toString()
			val proxRate = uic.fsenEtProxRate.text.toString().toInt()
			if (dG_prox3 == null || dG_prox3!!.st.address() != fileName) {
				val st_prox = SendTask_File(fileName, this.context!!)
				dG_prox3 = DataGetter(this.context!!, Sensor.TYPE_PROXIMITY, st_prox, proxRate)
			}
			startStopDataGetter(freq, dG_prox3!!, uic.fsenTbtnProxToFile)
		}
		uic.fsenTbtnAcceToFile.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val fileName = uic.fsenEtAcceFile.text.toString()
			val acceRate = uic.fsenEtAcceRate.text.toString().toInt()
			if (dG_acce3 == null || dG_acce3!!.st.address() != fileName) {
				val st_acce = SendTask_File(fileName, context)
				dG_acce3 = DataGetter(this.context!!, Sensor.TYPE_ACCELEROMETER, st_acce, acceRate)
			}
			startStopDataGetter(freq, dG_acce3!!, uic.fsenTbtnAcceToFile)
		}
		uic.fsenTbtnGyroToFile.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val fileName = uic.fsenEtGyroFile.text.toString()
			val gyroRate = uic.fsenEtGyroRate.text.toString().toInt()
			if (dG_gyro3 == null || dG_gyro3!!.st.address() != fileName) {
				val st_gyro = SendTask_File(fileName, context)
				dG_gyro3 = DataGetter(context!!, Sensor.TYPE_GYROSCOPE, st_gyro, gyroRate)
			}
			startStopDataGetter(freq, dG_gyro3!!, uic.fsenTbtnGyroToFile)
		}
		uic.fsenTbtnLightToFile.setOnClickListener{
			val freq = uic.fsenEtFreqSr.text.toString().toInt()
			val fileName = uic.fsenEtLightFile.text.toString()
			val lightRate = uic.fsenEtLightRate.text.toString().toInt()
			if (dG_light3 == null || dG_light3!!.st.address() != fileName) {
				val st_light = SendTask_File(fileName, context)
				dG_light3 = DataGetter(context!!, Sensor.TYPE_LIGHT, st_light, lightRate)
			}
			startStopDataGetter(freq, dG_light3!!, uic.fsenTbtnLightToFile)
		}
		return uic
	}

	private fun startStopDataGetter(freq: Int, dg: DataGetter, btn: ToggleButton) {
		if (btn.isChecked) dg.start(freq)
		else dg.stop()
	}
}

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

	private lateinit var uic: View

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val uic = inflater.inflate(R.layout.frag_recorder, container, false)
		ActivityCompat.requestPermissions(this.activity!!, arrayOf(Manifest.permission.RECORD_AUDIO), REQUEST_RECORD_AUDIO_PERMISSION)
		uic.frecTbtnSetTime.setOnClickListener {
			if (uic.frecTbtnSetTime.isChecked) {
				val frag = FragmentTimePicker(data_time)
				frag.show(fragmentManager, "Time Picker")
			} else {
				data_time.text = getString(R.string.time_init_text)
			}
		}
		uic.frecTbtnRecord.setOnClickListener{
			if (uic.frecTbtnRecord.isChecked) {
				rec = makeRecorder()
				Handler().postDelayed(Runnable { rec!!.startRecording() }, timeSourceDelay())
			} else {
				rec!!.stopRecording()
			}
		}
		return uic
	}

	private fun timeSourceDelay(): Long {
		if (!uic.frecTbtnSetTime.isChecked) return 0
		var delay = 100L
		// TODO
		return delay
	}

	private fun soundSource(): Int = when(uic.frecSbSoundSource.selectedItem.toString()) {
		resources.getStringArray(R.array.audioSources)[1] -> MediaRecorder.AudioSource.MIC
		resources.getStringArray(R.array.audioSources)[2] -> MediaRecorder.AudioSource.CAMCORDER
		resources.getStringArray(R.array.audioSources)[3] -> MediaRecorder.AudioSource.VOICE_UPLINK
		else -> MediaRecorder.AudioSource.DEFAULT
	}

	private fun makeRecorder(): Record {
		// TODO multiple sources ...
		val r = Record()
		r.setAudioChannels(uic.frecEtChannels.text as Int)
		r.setAudioSamplingRate(uic.frecEtSampleRate.text as Int)
		r.setOutputFile(uic.frecEtFileName.text as String)
		r.setAudioSource(soundSource())
		return r
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

class FragmentTimeSync() : Fragment() {

	private lateinit var uic: View

	inner class InitTime(): AsyncTask<String, Int, Int>() {

		override fun doInBackground(vararg params: String?): Int {
			TrueTimeRx.build().initializeRx(if (params.size > 0) params[0] else "time.google.com").subscribeOn(Schedulers.io()).subscribe()
			return 0
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		uic = inflater.inflate(R.layout.frag_time, container, false)
		InitTime().execute(uic.ftimEtNTPServer.text.toString())
		uic.ftimBtnGetTime.setOnClickListener {
			val cal = Calendar.getInstance()
			val t1 = cal.time
			val t2 = TrueTimeRx.now()
			uic.ftimEtPhoneTime.text = getDateStr(cal)
			cal.time = t2
			uic.ftimEtServerTime.text = getDateStr(cal)
			cal.time = Date(if (t1.time > t2.time) t1.time - t2.time else t2.time - t1.time)
			uic.ftimEtTimeDiff.text = getDateStr(cal)
		}
		return uic
	}

	private fun getDateStr(cal: Calendar): String {
		val a = getDateArr(cal)
		return "%2d:%2d:%2d.%3d".format(a[0], a[1], a[2], a[3])
	}

	private fun getDateArr(cal: Calendar): Array<Int> = arrayOf<Int>(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND))

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
	}
}
