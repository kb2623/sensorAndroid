package org.example.klemen.sensorandroid

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ToggleButton


@Suppress("CAST_NEVER_SUCCEEDS")
class FragmentRecorder : Fragment() {

	companion object Static {
		val LOG_TAG = "FragmentRecorder"
		val REQUEST_RECORD_AUDIO_PERMISSION = 200
	}

	private var canRecord = false
	private var rec: Record? = null

	class Record(var nFileName: String, var mRecorder: MediaRecorder) {

		var recording = false

		fun startRecording() {
			if (recording) return
			mRecorder.prepare()
			mRecorder.start()
			recording = true
		}

		fun stopRecording() {
			if (!recording) return
			mRecorder.stop()
			mRecorder.release()
			recording = false
		}
	}

	private lateinit var data_sampleRate: EditText
	private lateinit var data_fileName: EditText
	private lateinit var data_channels: EditText
	private lateinit var tb_record: ToggleButton

	override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
		val v = inflater.inflate(R.layout.frag_recorder, container, false)
		if (!canRecord) return v

		ActivityCompat.requestPermissions(this.activity!!, Manifest.permission.RECORD_AUDIO as Array<out String>, REQUEST_RECORD_AUDIO_PERMISSION)

		data_sampleRate = v.findViewById(R.id.et_sampleRate)
		data_fileName = v.findViewById(R.id.et_fileName)
		data_channels = v.findViewById(R.id.et_channels)
		tb_record = v.findViewById(R.id.tb_record)

		tb_record.setOnClickListener({
			// TODO
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