package com.sleeperbaby.app.audio

fun interface AudioSource {
    fun fill(buffer: FloatArray)
}
