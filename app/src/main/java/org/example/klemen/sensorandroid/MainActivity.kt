package org.example.klemen.sensorandroid

import android.annotation.SuppressLint
import android.hardware.Sensor
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import android.view.*
import android.widget.EditText
import android.widget.TextView
import android.widget.ToggleButton
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.frag_main.view.*
import kotlinx.android.synthetic.main.frag_sensors.view.*
import java.net.URL

class MainActivity : AppCompatActivity() {

	/**
	 * The [android.support.v4.view.PagerAdapter] that will provide
	 * fragments for each of the sections. We use a
	 * {@link FragmentPagerAdapter} derivative, which will keep every
	 * loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a
	 * [android.support.v4.app.FragmentStatePagerAdapter].
	 */
	private var mSectionsPagerAdapter: SectionsPagerAdapter? = null

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)
		// Create the adapter that will return a fragment for each of the three
		// primary sections of the activity.
		mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
		// Set up the ViewPager with the sections adapter.
		container.adapter = mSectionsPagerAdapter

		container.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
		tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(container))
		fab.setOnClickListener { view ->
			Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
					.setAction("Action", null).show()
		}
	}


	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		// Inflate the menu; this adds items to the action bar if it is present.
		menuInflater.inflate(R.menu.menu_main, menu)
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		val id = item.itemId
		if (id == R.id.action_settings) {
			return true
		}
		return super.onOptionsItemSelected(item)
	}


	/**
	 * A [FragmentPagerAdapter] that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

		private var buff = listOf<Fragment>(PlaceholderFragment.newInstance(0), SensorControler(), PlaceholderFragment.newInstance(2))

		override fun getItem(position: Int): Fragment {
			return buff[position]
		}

		override fun getCount(): Int {
			return buff.size
		}
	}
}

/**
 * A placeholder fragment containing a simple view.
 */
class PlaceholderFragment : Fragment() {

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val rootView = inflater.inflate(R.layout.frag_main, container, false)
		rootView.section_label.text = getString(R.string.section_format, arguments?.getInt(ARG_SECTION_NUMBER))
		return rootView
	}

	companion object {
		/**
		 * The fragment argument representing the section number for this
		 * fragment.
		 */
		private const val ARG_SECTION_NUMBER = "section_number"

		/**
		 * Returns a new instance of this fragment for the given section
		 * number.
		 */
		fun newInstance(sectionNumber: Int): PlaceholderFragment {
			val fragment = PlaceholderFragment()
			val args = Bundle()
			args.putInt(ARG_SECTION_NUMBER, sectionNumber)
			fragment.arguments = args
			return fragment
		}
	}
}

@Suppress("UNCHECKED_CAST", "CAST_NEVER_SUCCEEDS")
class SensorControler : Fragment() {

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
