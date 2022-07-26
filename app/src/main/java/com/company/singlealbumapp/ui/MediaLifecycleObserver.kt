package com.company.singlealbumapp.ui

import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

class MediaLifecycleObserver : LifecycleEventObserver {
    var player: MediaPlayer? = MediaPlayer()

    fun play() {
        player?.prepare()
        player?.setOnPreparedListener {
            it.start()
        }
    }

    fun resume() {
        player?.start()
    }

    fun pause() {
        player?.pause()
    }

    fun reset() {
        player?.reset()
    }

    fun stop() {
        player?.stop()
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_PAUSE -> player?.pause()
            Lifecycle.Event.ON_DESTROY -> {
                player?.release()
                player = null
                source.lifecycle.removeObserver(this)
            }
            else -> Unit
        }
    }
}