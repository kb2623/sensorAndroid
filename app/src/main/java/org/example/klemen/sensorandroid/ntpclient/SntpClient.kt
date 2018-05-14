@file:Suppress("UNUSED_CHANGED_VALUE", "NO_REFLECTION_IN_CLASS_PATH", "NAME_SHADOWING")

package org.example.klemen.sensorandroid.ntpclient

import android.os.SystemClock
import android.util.Log
import org.example.klemen.sensorandroid.InvalidNtpServerResponseException
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import kotlin.experimental.and

class SntpClient {

	companion object {
		const val RESPONSE_INDEX_ORIGINATE_TIME = 0
		const val RESPONSE_INDEX_RECEIVE_TIME = 1
		const val RESPONSE_INDEX_TRANSMIT_TIME = 2
		const val RESPONSE_INDEX_RESPONSE_TIME = 3
		const val RESPONSE_INDEX_ROOT_DELAY = 4
		const val RESPONSE_INDEX_DISPERSION = 5
		const val RESPONSE_INDEX_STRATUM = 6
		const val RESPONSE_INDEX_RESPONSE_TICKS = 7
		const val RESPONSE_INDEX_SIZE = 8
		// Log TAG
		val TAG = SntpClient::class.simpleName
		//
		const val NTP_PORT = 123
		const val NTP_MODE = 3
		const val NTP_VERSION = 3
		const val NTP_PACKET_SIZE = 48
		//
		const val INDEX_VERSION = 0
		const val INDEX_ROOT_DELAY = 4
		const val INDEX_ROOT_DISPERSION = 8
		const val INDEX_ORIGINATE_TIME = 24
		const val INDEX_RECEIVE_TIME = 32
		const val INDEX_TRANSMIT_TIME = 40
		// 70 years plus 17 leap days
		private const val OFFSET_1900_TO_1970 = ((365L * 70L) + 17L) * 24L * 60L * 60L
		/**
		 * See δ :
		 * https://en.wikipedia.org/wiki/Network_Time_Protocol#Clock_synchronization_algorithm
		 */
		fun getRoundTripDelay(response: Array<Long>): Long {
			return (response[RESPONSE_INDEX_RESPONSE_TIME] - response[RESPONSE_INDEX_ORIGINATE_TIME]) - (response[RESPONSE_INDEX_TRANSMIT_TIME] - response[RESPONSE_INDEX_RECEIVE_TIME])
		}
		/**
		 * See θ :
		 * https://en.wikipedia.org/wiki/Network_Time_Protocol#Clock_synchronization_algorithm
		 */
		fun getClockOffset(response: Array<Long>): Long {
			return ((response[RESPONSE_INDEX_RECEIVE_TIME] - response[RESPONSE_INDEX_ORIGINATE_TIME]) + (response[RESPONSE_INDEX_TRANSMIT_TIME] - response[RESPONSE_INDEX_RESPONSE_TIME])) / 2
		}
	}
	private var _cachedDeviceUptime = 0L
	private var _cachedSntpTime = 0L
	private var _sntpInitialized = false
	/**
	 * Sends an NTP request to the given host and processes the response.
	 *
	 * @param ntpHost           host name of the server.
	 */
	@Synchronized
	fun requestTime(ntpHost: String, rootDelayMax: Float, rootDispersionMax: Float, serverResponseDelayMax: Int, timeoutInMillis: Int): Array<Long> {
		val socket: DatagramSocket? = null
		try {
			var buffer = Array<Byte>(NTP_PACKET_SIZE, { _ -> 0})
			val address = InetAddress.getByName(ntpHost)
			val request = DatagramPacket(buffer.toByteArray(), buffer.size, address, NTP_PORT)
			writeVersion(buffer)
			// -----------------------------------------------------------------------------------
			// get current time and write it to the request packet
			val requestTime = System.currentTimeMillis()
			val requestTicks = SystemClock.elapsedRealtime()
			writeTimeStamp(buffer, INDEX_TRANSMIT_TIME, requestTime)
			val socket = DatagramSocket()
			socket.soTimeout = timeoutInMillis
			socket.send(request)
			// -----------------------------------------------------------------------------------
			// read the response
			var t = Array<Long>(RESPONSE_INDEX_SIZE, { _ -> 0})
			val response = DatagramPacket(buffer.toByteArray(), buffer.size)
			socket.receive(response)
			val responseTicks = SystemClock.elapsedRealtime()
			t[RESPONSE_INDEX_RESPONSE_TICKS] = responseTicks
			// -----------------------------------------------------------------------------------
			// extract the results
			// See here for the algorithm used:
			// https://en.wikipedia.org/wiki/Network_Time_Protocol#Clock_synchronization_algorithm
			val originateTime = readTimeStamp(buffer, INDEX_ORIGINATE_TIME)
			val receiveTime = readTimeStamp(buffer, INDEX_RECEIVE_TIME)
			val transmitTime = readTimeStamp(buffer, INDEX_TRANSMIT_TIME)
			val responseTime = requestTime + (responseTicks - requestTicks)
			t[RESPONSE_INDEX_ORIGINATE_TIME] = originateTime
			t[RESPONSE_INDEX_RECEIVE_TIME] = receiveTime
			t[RESPONSE_INDEX_TRANSMIT_TIME] = transmitTime
			t[RESPONSE_INDEX_RESPONSE_TIME] = responseTime
			// -----------------------------------------------------------------------------------
			// check validity of response
			t[RESPONSE_INDEX_ROOT_DELAY] = read(buffer, INDEX_ROOT_DELAY)
			val rootDelay = doubleMillis(t[RESPONSE_INDEX_ROOT_DELAY])
			if (rootDelay > rootDelayMax) {
				throw InvalidNtpServerResponseException("Invalid response from NTP server. %s violation. %f [actual] > %f [expected]", "root_delay", rootDelay.toFloat(), rootDelayMax)
			}
			t[RESPONSE_INDEX_DISPERSION] = read(buffer, INDEX_ROOT_DISPERSION)
			val rootDispersion = doubleMillis(t[RESPONSE_INDEX_DISPERSION])
			if (rootDispersion > rootDispersionMax) {
				throw InvalidNtpServerResponseException("Invalid response from NTP server. %s violation. %f [actual] > %f [expected]", "root_dispersion", rootDispersion.toFloat(), rootDispersionMax)
			}
			val mode = buffer[0] and 0x7
			if (mode != 4.toByte() && mode != 5.toByte()) {
				throw InvalidNtpServerResponseException("untrusted mode value for TrueTime: $mode")
			}
			val stratum = buffer[1] and 0xff.toByte()
			t[RESPONSE_INDEX_STRATUM] = stratum.toLong()
			if (stratum < 1 || stratum > 15) {
				throw InvalidNtpServerResponseException("untrusted stratum value for TrueTime: $stratum")
			}
			val leap =  (buffer[0].toInt() shr 6) and 0x3
			if (leap == 3) {
				throw InvalidNtpServerResponseException("unsynchronized server responded for TrueTime")
			}
			val delay = Math.abs((responseTime - originateTime) - (transmitTime - receiveTime))
			if (delay >= serverResponseDelayMax) {
				throw InvalidNtpServerResponseException("%s too large for comfort %f [actual] >= %f [expected]", "server_response_delay", delay.toFloat(), serverResponseDelayMax.toFloat())
			}
			val timeElapsedSinceRequest = Math.abs(originateTime - System.currentTimeMillis())
			if (timeElapsedSinceRequest >= 10_000) {
				throw InvalidNtpServerResponseException("Request was sent more than 10 seconds back $timeElapsedSinceRequest")
			}
			_sntpInitialized = true
			Log.i(TAG, "---- SNTP successful response from $ntpHost")
			// -----------------------------------------------------------------------------------
			// TODO:
			cacheTrueTimeInfo(t)
			return t
		} catch (e: Exception) {
			Log.d(TAG, "---- SNTP request failed for $ntpHost")
			throw e
		} finally {
			// FIXME KotlinNullPointerException on something
			socket!!.close()
		}
	}

	@Synchronized
	fun cacheTrueTimeInfo(response: Array<Long>) {
		_cachedSntpTime = sntpTime(response)
		_cachedDeviceUptime = response[RESPONSE_INDEX_RESPONSE_TICKS]
	}

	private fun sntpTime(response: Array<Long>): Long {
		val clockOffset = getClockOffset(response)
		val responseTime = response[RESPONSE_INDEX_RESPONSE_TIME]
		return responseTime + clockOffset
	}

	@Synchronized
	fun wasInitialized(): Boolean {
		return _sntpInitialized
	}
	/**
	 * @return time value computed from NTP server response
	 */
	@Synchronized
	fun getCachedSntpTime(): Long {
		return _cachedSntpTime
	}
	/**
	 * @return device uptime computed at time of executing the NTP request
	 */
	fun getCachedDeviceUptime(): Long {
		return _cachedDeviceUptime
	}

	// -----------------------------------------------------------------------------------
	// private helpers
	/**
	 * Writes NTP version as defined in RFC-1305
	 */
	private fun writeVersion(buffer: Array<Byte>) {
		// mode is in low 3 bits of first byte
		// version is in bits 3-5 of first byte
		buffer[INDEX_VERSION] = (NTP_MODE or (NTP_VERSION shl 3)).toByte()
	}

	/**
	 * Writes system time (milliseconds since January 1, 1970)
	 * as an NTP time stamp as defined in RFC-1305
	 * at the given offset in the buffer
	 */
	private fun writeTimeStamp(ibuffer: Array<Byte>, ioffset: Int, time: Long) {
		var buffer = ibuffer
		var offset = ioffset
		var seconds = time / 1000L
		val milliseconds = time - seconds * 1000L
		// consider offset for number of seconds
		// between Jan 1, 1900 (NTP epoch) and Jan 1, 1970 (Java epoch)
		seconds += OFFSET_1900_TO_1970
		// write seconds in big endian format
		buffer[offset++] = (seconds shr 24).toByte()
		buffer[offset++] = (seconds shr 16).toByte()
		buffer[offset++] = (seconds shr 8).toByte()
		buffer[offset++] = (seconds shr 0).toByte()
		val fraction = milliseconds * 0x100000000L / 1000L
		// write fraction in big endian format
		buffer[offset++] = (fraction shr 24).toByte()
		buffer[offset++] = (fraction shr 16).toByte()
		buffer[offset++] = (fraction shr 8).toByte()
		// low order bits should be random data
		buffer[offset++] = (Math.random() * 255.0).toByte()
	}
	/**
	 * @param offset offset index in buffer to start reading from
	 * @return NTP timestamp in Java epoch
	 */
	private fun readTimeStamp(buffer: Array<Byte>, offset: Int): Long {
		val seconds = read(buffer, offset)
		val fraction = read(buffer, offset + 4)
		return ((seconds - OFFSET_1900_TO_1970) * 1000) + ((fraction * 1000L) / 0x100000000L)
	}
	/**
	 * Reads an unsigned 32 bit big endian number
	 * from the given offset in the buffer
	 *
	 * @return 4 bytes as a 32-bit long (unsigned big endian)
	 */
	private fun read(buffer: Array<Byte>, offset: Int): Long{
		val b0 = buffer[offset]
		val b1 = buffer[offset + 1]
		val b2 = buffer[offset + 2]
		val b3 = buffer[offset + 3]
		return ((ui(b0) shl 24) + (ui(b1) shl 16) + (ui(b2) shl 8) + ui(b3)).toLong()
	}
	/***
	 * Convert (signed) byte to an unsigned int
	 *
	 * Java only has signed types so we have to do
	 * more work to get unsigned ops
	 *
	 * @param b input byte
	 * @return unsigned int value of byte
	 */
	private fun ui(b: Byte): Int {
		return (b and 0xFF.toByte()).toInt()
	}

	/**
	 * Used for root delay and dispersion
	 *
	 * According to the NTP spec, they are in the NTP Short format
	 * viz. signed 16.16 fixed point
	 *
	 * @param fix signed fixed point number
	 * @return as a double in milliseconds
	 */
	private fun doubleMillis(fix: Long): Double {
		return fix / 65.536
	}
}