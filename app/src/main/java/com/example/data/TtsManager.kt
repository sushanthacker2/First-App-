package com.example.data

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class TtsManager(context: Context) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = TextToSpeech(context.applicationContext, this)
    private var isInitialized = false

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _speed = MutableStateFlow(1.0f)
    val speed: StateFlow<Float> = _speed.asStateFlow()

    private val _pitch = MutableStateFlow(1.0f)
    val pitch: StateFlow<Float> = _pitch.asStateFlow()

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isInitialized = true
            tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {
                    _isPlaying.value = true
                }

                override fun onDone(utteranceId: String?) {
                    _isPlaying.value = false
                }

                @Deprecated("Deprecated in Java")
                override fun onError(utteranceId: String?) {
                    _isPlaying.value = false
                }
            })
        }
    }

    fun speak(text: String) {
        if (!isInitialized) return
        stop()
        tts?.setSpeechRate(_speed.value)
        tts?.setPitch(_pitch.value)
        // Strip out brackets/directions for clean natural speech
        val cleanedText = text
            .replace(Regex("\\[.*?\\]"), "")
            .replace(Regex("\\(.*?\\)"), "")
            .trim()
        val textToSpeak = if (cleanedText.isNotBlank()) cleanedText else text
        tts?.speak(textToSpeak, TextToSpeech.QUEUE_FLUSH, null, "creator_utterance")
    }

    fun stop() {
        tts?.stop()
        _isPlaying.value = false
    }

    fun setSpeed(rate: Float) {
        _speed.value = rate
        tts?.setSpeechRate(rate)
    }

    fun setPitch(pitchValue: Float) {
        _pitch.value = pitchValue
        tts?.setPitch(pitchValue)
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
        tts = null
    }
}
