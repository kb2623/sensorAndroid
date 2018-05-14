@file:Suppress("NO_REFLECTION_IN_CLASS_PATH")

package org.example.klemen.sensorandroid.ntpclient

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.os.SystemClock
import android.util.Log

class DiskCacheClient {

	companion object {
		val _package = DiskCacheClient::class.java.`package`.name
		//
		val KEY_CACHED_SHARED_PREFS = "${_package}.shared_preferences"
		val KEY_CACHED_BOOT_TIME = "${_package}.cached_boot_time"
		val KEY_CACHED_DEVICE_UPTIME = "${_package}.cached_device_uptime"
		val KEY_CACHED_SNTP_TIME = "${_package}.cached_sntp_time"
		//
		val TAG = DiskCacheClient::class.simpleName
	}

	var _sharedPreferences: SharedPreferences? = null

	fun enableDiskCaching(context: Context) {
		_sharedPreferences = context.getSharedPreferences(KEY_CACHED_SHARED_PREFS, MODE_PRIVATE)
	}

	fun clearCachedInfo(context: Context) {
		val sharedPreferences = context.getSharedPreferences(KEY_CACHED_SHARED_PREFS, MODE_PRIVATE)
		if (sharedPreferences == null) return
		sharedPreferences.edit().clear().apply()
	}

	fun cacheTrueTimeInfo(sntpClient: SntpClient) {
		if (sharedPreferencesUnavailable()) return
		val cachedSntpTime = sntpClient.getCachedSntpTime()
		val cachedDeviceUptime = sntpClient.getCachedDeviceUptime()
		val bootTime = cachedSntpTime - cachedDeviceUptime
		Log.d(TAG, String.format("Caching true time info to disk sntp [%s] device [%s] boot [%s]", cachedSntpTime, cachedDeviceUptime, bootTime))
		_sharedPreferences!!.edit().putLong(DiskCacheClient.KEY_CACHED_BOOT_TIME, bootTime).apply()
		_sharedPreferences!!.edit().putLong(DiskCacheClient.KEY_CACHED_DEVICE_UPTIME, cachedDeviceUptime).apply()
		_sharedPreferences!!.edit().putLong(DiskCacheClient.KEY_CACHED_SNTP_TIME, cachedSntpTime).apply()
	}

	fun isTrueTimeCachedFromAPreviousBoot(): Boolean {
		if (sharedPreferencesUnavailable()) return false
		val cachedBootTime = _sharedPreferences!!.getLong(DiskCacheClient.KEY_CACHED_BOOT_TIME, 0L)
		if (cachedBootTime == 0L) {
			return false
			// has boot time changed (simple check)
		}
		val bootTimeChanged = SystemClock.elapsedRealtime() < getCachedDeviceUptime()
		Log.i(TAG, "---- boot time changed " + bootTimeChanged)
		return !bootTimeChanged
	}

	fun getCachedDeviceUptime(): Long {
		if (sharedPreferencesUnavailable()) return 0L
		return _sharedPreferences!!.getLong(DiskCacheClient.KEY_CACHED_DEVICE_UPTIME, 0L)
	}

	fun getCachedSntpTime(): Long {
		if (sharedPreferencesUnavailable()) return 0L
		return _sharedPreferences!!.getLong(DiskCacheClient.KEY_CACHED_SNTP_TIME, 0L)
	}

	private fun sharedPreferencesUnavailable(): Boolean {
		if (_sharedPreferences == null) {
			Log.w(TAG, "Cannot use disk caching strategy for TrueTime. SharedPreferences unavailable");
			return true
		}
		return false
	}
}