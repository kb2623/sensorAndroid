package org.example.klemen.sensorandroid

import android.content.Context
import android.os.AsyncTask
import android.util.Log
import org.json.JSONObject
import java.io.File
import java.io.OutputStreamWriter
import java.io.PrintWriter
import java.net.HttpURLConnection
import java.net.Socket
import java.net.URL
import java.util.*

/**
 * Created by klemen on 7.12.2017.
 */
abstract class SendTask<P, R>: AsyncTask<List<Data>, P, R>() {
	var Ex: Exception? = null
	abstract fun copy(): SendTask<P, R>
	abstract fun address(): String
	abstract fun close()
}

data class Data(val v: Array<out Any>, val time: Long) {
	override fun toString(): String {
		return v.joinToString() + ", " + time
	}

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (javaClass != other?.javaClass) return false
		other as Data
		if (!Arrays.equals(v, other.v)) return false
		if (time != other.time) return false
		return true
	}

	override fun hashCode(): Int {
		var result = Arrays.hashCode(v)
		result = 31 * result + time.hashCode()
		return result
	}
}

class SendTask_JSON_HTTP(private val url: URL, private val ofilds: Array<String>)  : SendTask<Void, Int>() {

	override fun doInBackground(vararg p0: List<Data>?): Int? {
		try {
			for (ldata in p0) {
				val obj = JSONObject()
				ldata?.withIndex()?.forEach {(i, e) ->
					val jobj = JSONObject()
					e.v.withIndex().forEach {(i, e) -> jobj.put(ofilds[i], e)}
					jobj.put("time", e.time)
					obj.put(i.toString(), jobj)
				}
				val conn = url.openConnection() as HttpURLConnection
				conn.doOutput = true
				conn.doInput = true
				conn.setRequestProperty("Content-Type", "application/json")
				conn.setRequestProperty("Accept", "application/json")
				conn.requestMethod = "POST"
				val wr = OutputStreamWriter(conn.outputStream)
				wr.write(obj.toString())
				wr.flush()
				if (conn.responseCode != HttpURLConnection.HTTP_OK) Log.d("D", "OK")
				else Log.d("E", "ERROR send")
			}
			return 0
		} catch (e: Exception) {
			Log.e("E", "ERROR Send: " + e.message)
			Ex = e
		}
		return 1
	}

	override fun address(): String {
		return url.toString()
	}

	override fun copy(): SendTask<Void, Int> {
		return SendTask_JSON_HTTP(url, ofilds)
	}

	override fun close() {
		Log.d("D", "End of sending wiht HTTP/JSON")
	}

}

class SendTask_Socket(IipAddress: String, Iport: Int = -1) : SendTask<Void, Void>() {

	private var ipAddress = ""
	private var port = 0

	init {
		if (Iport <= -1) {
			val ri = IipAddress.lastIndexOf(':')
			if (ri >= 0) {
				ipAddress = IipAddress.substring(0, ri)
				port = IipAddress.substring(ri + 1, IipAddress.length).toInt()
			}
		} else {
			ipAddress = IipAddress
			port = Iport
		}
	}

	override fun doInBackground(vararg p0: List<Data>?): Void? {
		for (dd in p0) {
			try {
				val s = Socket(ipAddress, port)
				val pw = OutputStreamWriter(s.getOutputStream(), "UTF-8")
				for (e in dd!!) {
					pw.write(e.toString() + '\n', 0, e.toString().length + 1)
				}
				pw.flush()
				pw.close()
				s.close()
			} catch (e: Exception) {
				Log.e("E", "Error send: " + e.message)
				Ex = e
			}
		}
		return null
	}

	override fun address(): String {
		return ipAddress + ":" + port
	}

	override fun copy(): SendTask<Void, Void> {
		return SendTask_Socket(ipAddress, port)
	}

	override fun close() {
		Log.d("D", "End of sending wiht socket")
	}

}


class SendTask_File(val fileName: String?, context: Context?) : SendTask<Void, Void>() {

	private lateinit var pw: PrintWriter

	init {
		if (fileName != null && context != null) {
			val file = File(context.filesDir, fileName)
			pw = PrintWriter(file.outputStream())
		}
	}

	constructor(pw: PrintWriter, fileName: String?) : this(fileName, null) {
		this.pw = pw
	}

	override fun address(): String {
		return fileName!!
	}

	override fun doInBackground(vararg p0: List<Data>?): Void? {
		for (dd in p0) {
			try {
				for (e in dd!!) {
					pw.write(e.toString() + '\n')
				}
			} catch (e: Exception) {
				Log.e("E", "Error send: " + e.message)
				Ex = e
			}
		}
		return null
	}

	override fun copy(): SendTask<Void, Void> {
		return SendTask_File(pw, fileName)
	}

	override fun close() {
		pw.flush()
		pw.close()
	}
}
