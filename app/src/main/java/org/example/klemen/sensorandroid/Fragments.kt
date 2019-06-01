@file:Suppress("unused", "DEPRECATION", "CAST_NEVER_SUCCEEDS", "MemberVisibilityCanBePrivate", "PrivatePropertyName", "UNUSED_VARIABLE", "UNUSED_ANONYMOUS_PARAMETER", "UNCHECKED_CAST", "LocalVariableName", "RedundantOverride", "PLATFORM_CLASS_MAPPED_TO_KOTLIN")

package org.example.klemen.sensorandroid

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.ActivityCompat
import android.support.v4.app.DialogFragment
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.TimePicker
import android.widget.Toast
import android.widget.ToggleButton
import com.instacart.library.truetime.TrueTimeRx
import kotlinx.android.synthetic.main.frag_main.view.*
import kotlinx.android.synthetic.main.frag_recorder.*
import kotlinx.android.synthetic.main.frag_recorder.view.*
import kotlinx.android.synthetic.main.frag_sensors.view.*
import kotlinx.android.synthetic.main.frag_time.view.*
import java.lang.Math.abs
import java.net.URL
import java.lang.Exception
import java.net.InetAddress
import java.util.*

class FragmentPlaceholder : Fragment() {

	companion object {
		val LOG_TAG = FragmentPlaceholder::class.simpleName
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

	var minute = 0
	var houre = 0

	@SuppressLint("ValidFragment")
	constructor(time_out: TextView) : this() {
		this.time_out = time_out
	}

	companion object {
		val LOG_TAG = FragmentTimePicker::class.simpleName
	}

	private var time_out: TextView? = null

	private val cal = Calendar.getInstance()

	override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
		val hour = cal.get(Calendar.HOUR_OF_DAY)
		val minute = cal.get(Calendar.MINUTE)
		return TimePickerDialog(activity, this, hour, minute, true)
	}

	override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
		this.minute = minute
		this.houre = hourOfDay
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
		val LOG_TAG = FragmentSensors::class.simpleName
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

	private fun pressSendUrl() {
		// FixME
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

	private fun pressSendSocket() {
		// FIXME
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

	private fun pressSaveToFile() {
		// FIXME
		val freq = uic.fsenEtFreqSr.text.toString().toInt()
		val fileName = uic.fsenEtProxFile.text.toString()
		val proxRate = uic.fsenEtProxRate.text.toString().toInt()
		if (dG_prox3 == null || dG_prox3!!.st.address() != fileName) {
			val st_prox = SendTask_File(fileName, this.context!!)
			dG_prox3 = DataGetter(this.context!!, Sensor.TYPE_PROXIMITY, st_prox, proxRate)
		}
		startStopDataGetter(freq, dG_prox3!!, uic.fsenTbtnProxToFile)
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val uic = inflater.inflate(R.layout.frag_sensors, container, false)
		// FIXME
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

/**
 * Pomoc pri implementaciji:
 * 	https://github.com/roman10/roman10-android-tutorial/blob/master/AndroidWaveRecorder/src/roman10/tutorial/androidwaverecorder/WavAudioRecorder.java
 * 	https://gist.github.com/kmark/d8b1b01fb0d2febf5770
 */
class FragmentRecorder : Fragment() {

	companion object {
		val LOG_TAG = FragmentRecorder::class.simpleName
		const val REQUEST_RECORD_AUDIO_PERMISSION = 200
	}

	private var canRecord = false
	private var rec: BgTaskRecorder? = null
	private lateinit var uic: View
	private var timeFrag: FragmentTimePicker? = null
	private var recEvent: Handler? = null

	private fun setupPermissions() {
		if (ContextCompat.checkSelfPermission(context!!, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)
		} else {
			canRecord = true
		}
	}

	private fun pressBtnSetTime() {
		if (uic.frecTbtnSetTime.isChecked) {
			timeFrag = FragmentTimePicker(frecTwRecordTime)
			timeFrag?.show(fragmentManager, "Time Picker")
		} else {
			frecTwRecordTime.text = getString(R.string.time_init_text)
		}
	}

	private fun pressBtnRecord() {
		if (uic.frecTbtnRecord.isChecked) {
			rec = createRecorder()
			val timeDelay = timeSourceDelay()
			recEvent = Handler()
			recEvent!!.postDelayed({
				rec?.execute("%s/%s.wav".format(context!!.filesDir.absolutePath, uic.frecEtFileName.text.toString()))
				uic.lblRecOutInfo.text = resources.getString(R.string.lbl_record)
			}, timeDelay)
			Toast.makeText(activity, "Time delay: %d ms".format(timeDelay), Toast.LENGTH_LONG).show()
		} else {
			rec?.cancel(true)
			recEvent?.removeCallbacksAndMessages(null)
			uic.lblRecOutInfo.text = resources.getString(R.string.lbl_empty)
		}
	}

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		uic = inflater.inflate(R.layout.frag_recorder, container, false)
		setupPermissions()
		uic.frecTbtnSetTime.setOnClickListener { pressBtnSetTime() }
		if (!canRecord) uic.frecTbtnRecord.isEnabled = false
		uic.frecTbtnRecord.setOnClickListener{ pressBtnRecord() }
		return uic
	}

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		when(requestCode) {
			REQUEST_RECORD_AUDIO_PERMISSION -> canRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED
		}
		if (!canRecord) Toast.makeText(activity, resources.getString(R.string.mess_can_not_rec), Toast.LENGTH_SHORT).show()
		else Toast.makeText(activity, resources.getString(R.string.mess_can_rec), Toast.LENGTH_SHORT).show()
	}

	private fun getCurrTime(): Date = when (uic.frecSbTimeSource.selectedItem.toString()) {
		resources.getStringArray(R.array.timeSources)[1] -> TrueTimeRx.now()
		else -> Calendar.getInstance().time
	}

	private fun timeSourceDelay(): Long {
		if (!uic.frecTbtnSetTime.isChecked) return 0L
		val cal = Calendar.getInstance()
		cal.set(Calendar.HOUR_OF_DAY, timeFrag?.houre!!)
		cal.set(Calendar.MINUTE, timeFrag?.minute!!)
		cal.set(Calendar.MILLISECOND, 0)
		return abs(cal.time.time - getCurrTime().time)
	}

	private fun soundSource(): Int {
		val arr = resources.getStringArray(R.array.audioSources)
		return when (uic.frecSbSoundSource.selectedItem.toString()) {
			arr[1] -> MediaRecorder.AudioSource.MIC
			arr[2] -> MediaRecorder.AudioSource.CAMCORDER
			arr[3] -> MediaRecorder.AudioSource.VOICE_UPLINK
			else -> MediaRecorder.AudioSource.DEFAULT
		}
	}

	private fun audioEncoder(): Int {
		val arr = resources.getStringArray(R.array.audioEncoder)
//		return when (uic.frecSbAudioEncoder.selectedItem.toString()) {
		return when ("Dela") {
			arr[0] -> MediaRecorder.AudioEncoder.AAC_ELD
			arr[1] -> MediaRecorder.AudioEncoder.AAC
			arr[2] -> MediaRecorder.AudioEncoder.AMR_NB
			arr[3] -> MediaRecorder.AudioEncoder.AMR_WB
			arr[4] -> MediaRecorder.AudioEncoder.HE_AAC
			arr[5] -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				MediaRecorder.AudioEncoder.VORBIS
			} else {
				TODO("VERSION.SDK_INT < LOLLIPOP")
			}
			else -> MediaRecorder.AudioEncoder.DEFAULT
		}
	}

	private fun outputFormat(): Int {
		val arr = resources.getStringArray(R.array.outputFormat)
//		return when (uic.frecSbOutputFormat.selectedItem.toString()) {
		return when ("Dela") {
			arr[0] -> MediaRecorder.OutputFormat.AAC_ADTS
			arr[1] -> MediaRecorder.OutputFormat.AMR_NB
			arr[2] -> MediaRecorder.OutputFormat.AMR_WB
			arr[3] -> MediaRecorder.OutputFormat.THREE_GPP
			arr[4] -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				MediaRecorder.OutputFormat.WEBM
			} else {
				TODO("VERSION.SDK_INT < LOLLIPOP")
			}
			arr[5] -> MediaRecorder.OutputFormat.MPEG_4
			arr[6] -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				MediaRecorder.OutputFormat.MPEG_2_TS
			} else {
				TODO("VERSION.SDK_INT < O")
			}
			else -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
				MediaRecorder.OutputFormat.MPEG_2_TS
			} else {
				TODO("VERSION.SDK_INT < O")
			}
		}
	}

	private fun audioFormat(): Int {
		val arr = resources.getStringArray(R.array.audioFormat)
		return when (uic.frecSbAudioFormat.selectedItem.toString()) {
			arr[0] -> AudioFormat.ENCODING_PCM_16BIT
			arr[1] -> AudioFormat.ENCODING_PCM_8BIT
			arr[2] -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
				AudioFormat.ENCODING_PCM_FLOAT
			} else {
				TODO("VERSION.SDK_INT < LOLLIPOP")
			}
			else -> AudioFormat.ENCODING_DEFAULT
		}
	}

	private fun audioFormatBits(): Int {
		val arr = resources.getStringArray(R.array.audioFormat)
		return when (uic.frecSbAudioFormat.selectedItem.toString()) {
			arr[0] -> 16
			arr[1] -> 8
			arr[2] -> 32
			else -> 0
		}
	}

	private fun audioChannelsProp(): Int {
		val arr = resources.getStringArray(R.array.audioChannels)
		return when (uic.frecSbChannels.selectedItem.toString()) {
			arr[1] -> AudioFormat.CHANNEL_IN_STEREO
			else -> AudioFormat.CHANNEL_IN_MONO
		}
	}

	private fun audioChannelsNus(): Int {
		val arr = resources.getStringArray(R.array.audioChannels)
		return when (uic.frecSbChannels.selectedItem.toString()) {
			arr[0] -> 1
			arr[1] -> 2
			else -> 1
		}
	}

	private fun createRecorder(): BgTaskRecorder {
		val sampleRate = uic.frecSbSampleRate.selectedItem.toString().toInt()
		val channels = audioChannelsNus()
		val channelsProp = audioChannelsProp()
		val mBitsPersample = audioFormatBits()
		val audioFormat = audioFormat()
		val mBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelsProp, audioFormat)
		val orec = BgTaskRecorderFile(soundSource(), sampleRate, audioChannelsProp(), audioFormat, mBufferSize)
		return orec
	}
}

class FragmentTimeSync : Fragment() {

	companion object {
		val LOG_TAG = FragmentTimeSync::class.simpleName
	}

	private lateinit var uic: View

	private fun pressStnGetTime() {
		val cal = Calendar.getInstance()
		val t1 = cal.time
		val t2 = TrueTimeRx.now()
		uic.ftimEtPhoneTime.text = getDateStr(cal)
		cal.time = t2
		uic.ftimEtServerTime.text = getDateStr(cal)
		cal.time = Date(abs(t1.time - t2.time))
		uic.ftimEtTimeDiff.text = getDateStr(cal)
	}

	override fun onCreateView(inflater:LayoutInflater, container:ViewGroup?, savedInstanceState: Bundle?): View? {
		uic = inflater.inflate(R.layout.frag_time, container, false)
		InitTime().execute(uic.ftimEtNTPServer.text.toString())
		uic.ftimBtnGetTime.setOnClickListener{ pressStnGetTime() }
		uic.ftimEtNTPServer.addTextChangedListener(object : TextWatcher{
			override fun afterTextChanged(p0: Editable?) {
				try {
					InetAddress.getByName(p0.toString()).isReachable(2000)
					InitTime().execute(p0.toString())
				} catch (error: Exception) {
					Log.d("SensorAndorid", error.toString())
				}
			}

			override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

			override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
		})
		return uic
	}

	private fun getDateStr(cal: Calendar): String {
		val a = getDateArr(cal)
		return "%2d:%2d:%2d.%3d".format(a[0], a[1], a[2], a[3])
	}

	private fun getDateArr(cal: Calendar): Array<Int> = arrayOf(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal.get(Calendar.MILLISECOND))

	override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults)
	}
}
