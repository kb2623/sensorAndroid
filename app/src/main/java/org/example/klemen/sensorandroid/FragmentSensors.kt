package org.example.klemen.sensorandroid

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import java.net.URL

@Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS", "PrivatePropertyName", "LocalVariableName")
class FragmentSensors : Fragment() {

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
