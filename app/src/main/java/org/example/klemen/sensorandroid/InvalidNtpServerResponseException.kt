package org.example.klemen.sensorandroid

import java.io.IOException
import java.util.*

class InvalidNtpServerResponseException(mess: String) : IOException(mess) {
	constructor(mess: String, property: String, actualValue: Float, expectedValue: Float) : this(String.format(Locale.getDefault(), mess, property, actualValue, expectedValue))
}