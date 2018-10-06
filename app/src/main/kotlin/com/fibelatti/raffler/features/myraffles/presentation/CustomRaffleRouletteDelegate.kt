package com.fibelatti.raffler.features.myraffles.presentation

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import androidx.lifecycle.LifecycleObserver
import com.fibelatti.raffler.R
import java.util.Random
import javax.inject.Inject

private const val ROULETTE_SPEED_MAX = 350L
private const val ROULETTE_SPEED_MIN = 1000L
private const val ROULETTE_SPEED_STEPS = 50L
private const val MEDIA_VOLUME_STEPS = 0.05F
private const val MEDIA_FADE_DELAY = 600L

class CustomRaffleRouletteDelegate @Inject constructor() : LifecycleObserver {
    private lateinit var mediaPlayer: MediaPlayer
    private var mediaVolume = 1F
    private var rouletteMusicEnabled: Boolean = false

    private var optionsCount = 0
    private var currentIndex = -1
    private var randomIndex = 0

    private var currentSpeed = 0L

    private val handler by lazy { Handler() }

    var status: RouletteStatus = RouletteStatus.PLAYING
        private set

    fun setup(
        context: Context,
        customRaffleModel: CustomRaffleModel,
        rouletteMusicEnabled: Boolean
    ): CustomRaffleRouletteDelegate = apply {
        this.optionsCount = customRaffleModel.items.size
        this.rouletteMusicEnabled = rouletteMusicEnabled
        this.mediaPlayer = MediaPlayer.create(context, R.raw.easter_egg_soundtrack).apply {
            setAudioAttributes(AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
        }
    }

    fun startRoulette(
        onRouletteStarted: () -> Unit,
        onRouletteIndexUpdated: (newIndex: Int) -> Unit
    ) {
        randomIndex = Random().nextInt(optionsCount)
        currentSpeed = ROULETTE_SPEED_MIN
        status = RouletteStatus.PLAYING
        mediaVolume = if (rouletteMusicEnabled) 1F else 0F
        mediaPlayer.apply {
            setVolume(mediaVolume, mediaVolume)
            start()
        }

        onRouletteStarted()

        animate(onRouletteIndexUpdated)
    }

    fun stopRoulette(onRouletteStopped: () -> Unit) {
        status = RouletteStatus.STOPPING
        fadeOutMusic(onRouletteStopped)
    }

    fun tearDown() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer.release()
    }

    private fun animate(onRouletteIndexUpdated: (newIndex: Int) -> Unit) {
        increaseIndex()

        onRouletteIndexUpdated(currentIndex)

        handler.postDelayed({ if (!shouldStop()) animate(onRouletteIndexUpdated) }, getCurrentSpeed())
    }

    private fun increaseIndex() {
        currentIndex++

        if (currentIndex == optionsCount) currentIndex = 0
        if (currentSpeed == ROULETTE_SPEED_MIN) currentIndex = randomIndex
    }

    private fun getCurrentSpeed(): Long {
        return when (status) {
            RouletteStatus.IDLE -> currentSpeed
            RouletteStatus.STOPPING -> decreaseSpeed()
            RouletteStatus.PLAYING -> increaseSpeed()
        }
    }

    private fun increaseSpeed(): Long =
        currentSpeed.apply { if (currentSpeed > ROULETTE_SPEED_MAX) currentSpeed -= ROULETTE_SPEED_STEPS }

    private fun decreaseSpeed(): Long =
        currentSpeed.apply { if (currentSpeed < ROULETTE_SPEED_MIN) currentSpeed += ROULETTE_SPEED_STEPS }

    private fun shouldStop(): Boolean = currentIndex == randomIndex && currentSpeed == ROULETTE_SPEED_MIN

    private fun fadeOutMusic(onRouletteStopped: () -> Unit) {
        mediaVolume -= MEDIA_VOLUME_STEPS
        mediaPlayer.setVolume(mediaVolume, mediaVolume)

        handler.postDelayed({
            if (shouldStop()) {
                mediaPlayer.run {
                    pause()
                    seekTo(0)
                }
                onRouletteStopped()
                status = RouletteStatus.IDLE
            } else {
                fadeOutMusic(onRouletteStopped)
            }
        }, MEDIA_FADE_DELAY)
    }

    enum class RouletteStatus {
        IDLE, PLAYING, STOPPING
    }
}
