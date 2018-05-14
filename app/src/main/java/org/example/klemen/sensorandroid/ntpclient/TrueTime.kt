@file:Suppress("UNUSED_PARAMETER")

package org.example.klemen.sensorandroid.ntpclient

import android.content.Context
import android.os.AsyncTask
import android.os.SystemClock
import android.util.Log
import java.util.*

class TrueTime {

	companion object {
		val TAG = TrueTime::class.simpleName

		val INSTANCE = TrueTime()
		val DISK_CACHE_CLIENT = DiskCacheClient()
		val SNTP_CLIENT = SntpClient()

		var _rootDelayMax = 100F
		var _rootDispersionMax = 100F
		var _serverResponseDelayMax = 200;
		var _udpSocketTimeoutInMillis = 30_000
		/**
		 * @return Date object that returns the current time in the default Timezone
		 */
		fun now(): Date {
			if (!isInitialized()) throw IllegalStateException ("You need to call init() on TrueTime at least once.")
			val cachedSntpTime = _getCachedSntpTime()
			val cachedDeviceUptime = _getCachedDeviceUptime()
			val deviceUptime = SystemClock.elapsedRealtime()
			val now = cachedSntpTime + (deviceUptime - cachedDeviceUptime)
			return Date(now)
		}

		fun isInitialized(): Boolean {
			return SNTP_CLIENT.wasInitialized() || DISK_CACHE_CLIENT.isTrueTimeCachedFromAPreviousBoot()
		}

		fun build(): TrueTime = INSTANCE

		fun clearCachedInfo(context: Context) {
			DISK_CACHE_CLIENT.clearCachedInfo(context)
		}

		@Synchronized
		fun saveTrueTimeInfoToDisk() {
			if (!SNTP_CLIENT.wasInitialized()) {
				Log.i(TAG, "---- SNTP client not available. not caching TrueTime info in disk")
				return
			}
			DISK_CACHE_CLIENT.cacheTrueTimeInfo(SNTP_CLIENT)
		}

		fun _getCachedDeviceUptime(): Long {
			val cachedDeviceUptime = if (SNTP_CLIENT.wasInitialized()) SNTP_CLIENT.getCachedDeviceUptime() else DISK_CACHE_CLIENT.getCachedDeviceUptime()
			if (cachedDeviceUptime == 0L) {
				throw RuntimeException("expected device time from last boot to be cached. couldn't find it.")
			}
			return cachedDeviceUptime
		}

		fun _getCachedSntpTime(): Long {
			val cachedSntpTime = if (SNTP_CLIENT.wasInitialized()) SNTP_CLIENT.getCachedSntpTime() else DISK_CACHE_CLIENT.getCachedSntpTime()
			if (cachedSntpTime == 0L) {
				throw RuntimeException("expected SNTP time from last boot to be cached. couldn't find it.");
			}
			return cachedSntpTime
		}
	}

	private var _ntpHost = "1.us.pool.ntp.org"

	fun initialize() {
		initialize(_ntpHost)
		saveTrueTimeInfoToDisk()
	}
	/**
	 * Cache TrueTime initialization information in SharedPreferences
	 * This can help avoid additional TrueTime initialization on app kills
	 */
	@Synchronized
	fun withSharedPreferences(context: Context): TrueTime {
		DISK_CACHE_CLIENT.enableDiskCaching(context)
		return INSTANCE
	}

	@Synchronized
	fun withConnectionTimeout(timeoutInMillis: Int): TrueTime {
		_udpSocketTimeoutInMillis = timeoutInMillis
		return INSTANCE
	}

	@Synchronized
	fun withRootDelayMax(rootDelayMax: Float): TrueTime {
		if (rootDelayMax > _rootDelayMax) {
			val log = String.format(Locale.getDefault(), "The recommended max rootDelay value is %f. You are setting it at %f", _rootDelayMax, rootDelayMax)
			Log.w(TAG, log)
		}
		_rootDelayMax = rootDelayMax
		return INSTANCE
	}

	@Synchronized
	fun withRootDispersionMax(rootDispersionMax: Float): TrueTime {
		if (rootDispersionMax > _rootDispersionMax) {
			val log = String.format(Locale.getDefault(), "The recommended max rootDispersion value is %f. You are setting it at %f", _rootDispersionMax, rootDispersionMax)
			Log.w(TAG, log)
		}
		_rootDispersionMax = rootDispersionMax
		return INSTANCE
	}

	@Synchronized
	fun withServerResponseDelayMax(serverResponseDelayInMillis: Int): TrueTime {
		_serverResponseDelayMax = serverResponseDelayInMillis
		return INSTANCE
	}

	@Synchronized
	fun withNtpHost(ntpHost: String): TrueTime {
		_ntpHost = ntpHost
		return INSTANCE
	}

	@Synchronized
	fun withLoggingEnabled(isLoggingEnabled: Boolean): TrueTime {
		// Log.setLoggingEnabled(isLoggingEnabled)
		return INSTANCE
	}

	fun initialize(ntpHost: String) {
		if (isInitialized()) {
			Log.i(TAG, "---- TrueTime already initialized from previous boot/init")
			return
		}
		requestTime(ntpHost)
	}

	fun requestTime(ntpHost: String): Array<Long> {
		return SNTP_CLIENT.requestTime(ntpHost, _rootDelayMax, _rootDispersionMax, _serverResponseDelayMax, _udpSocketTimeoutInMillis)
	}

	fun cacheTrueTimeInfo(response: Array<Long>) {
		SNTP_CLIENT.cacheTrueTimeInfo(response)
	}
}

class InitTrueTime(): AsyncTask<String, Void, Void>() {
	override fun doInBackground(vararg params: String?): Void? {
		if (params.size > 0) params[0]?.let { TrueTime.build().initialize(it)  }
		else TrueTime.build().initialize()
		return null
	}
}
