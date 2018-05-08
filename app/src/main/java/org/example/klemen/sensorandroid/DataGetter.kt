package org.example.klemen.sensorandroid

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager

/**
 * Created by klemen on 5.12.2017.
 */
class DataGetter(context: Context, private val
sensorType: Int, var st: SendTask<Void, Void>, private val buff_size: Int = 100) : SensorEventListener {

	private val buff: ArrayList<Data> = ArrayList(buff_size)

	private val sM: SensorManager by lazy {
		context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
	}

	fun start(rate: Int) {
		sM.registerListener(this, sM.getDefaultSensor(sensorType), rate)
	}

	fun stop() {
		if (buff.size > 0) st.execute(ArrayList(buff))
		st.close()
		st = st.copy()
		sM.unregisterListener(this)
	}

	override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
		// TODO("not implemented")
	}

	override fun onSensorChanged(p0: SensorEvent?) {
		val time = p0!!.timestamp
		val v = p0.values
		val a = when (sensorType) {
			Sensor.TYPE_ACCELEROMETER -> arrayOf(v[0], v[1], v[2])
			Sensor.TYPE_GYROSCOPE -> arrayOf(v[0], v[1], v[2])
			Sensor.TYPE_LIGHT -> arrayOf(v[0])
			Sensor.TYPE_PROXIMITY -> arrayOf(v[0])
			else -> arrayOf(0)
		}
		buff.add(Data(a, time))
		if (buff.size + 1 > buff_size) {
			st.execute(ArrayList(buff))
			st = st.copy()
			buff.clear()
		}
	}
}
