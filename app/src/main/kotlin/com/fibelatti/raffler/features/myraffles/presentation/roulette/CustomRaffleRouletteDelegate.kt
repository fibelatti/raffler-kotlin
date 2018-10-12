package com.fibelatti.raffler.features.myraffles.presentation.roulette

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Handler
import com.fibelatti.raffler.R
import com.fibelatti.raffler.core.extension.random
import com.fibelatti.raffler.features.myraffles.presentation.common.CustomRaffleModel
import javax.inject.Inject

private const val ROULETTE_SPEED_MAX = 350L
private const val ROULETTE_SPEED_MIN = 1000L
private const val ROULETTE_SPEED_STEPS = 50L
private const val MEDIA_VOLUME_STEPS = 0.05F

class CustomRaffleRouletteDelegate @Inject constructor() {
    private var mediaPlayer: MediaPlayer? = null
    private var mediaVolume = 1F

    private var optionsIndex: List<Int> = listOf()
    private var currentIndex = -1
    private var randomIndex = 0

    private var currentSpeed = 0L

    private val handler by lazy { Handler() }

    var status: RouletteStatus = RouletteStatus.IDLE
        private set

    fun setup(
        context: Context,
        rouletteMusicEnabled: Boolean
    ): CustomRaffleRouletteDelegate = apply {
        if (rouletteMusicEnabled) {
            this.mediaPlayer = MediaPlayer.create(context, R.raw.easter_egg_soundtrack).apply {
                setAudioAttributes(AudioAttributes.Builder().setLegacyStreamType(AudioManager.STREAM_MUSIC).build())
            }
        }
    }

    fun tearDown() {
        handler.removeCallbacksAndMessages(null)
        mediaPlayer?.release()
    }

    fun startRoulette(
        customRaffleModel: CustomRaffleModel,
        onRouletteStarted: () -> Unit,
        onRouletteIndexUpdated: (newIndex: Int) -> Unit,
        onRouletteStopped: (winningIndex: Int) -> Unit
    ) {
        optionsIndex = customRaffleModel.includedItemsIndex
        randomIndex = optionsIndex.random()
        currentSpeed = ROULETTE_SPEED_MIN
        status = RouletteStatus.PLAYING
        mediaVolume = 1F
        mediaPlayer?.apply {
            setVolume(mediaVolume, mediaVolume)
            start()
        }

        onRouletteStarted()

        animate(onRouletteIndexUpdated, onRouletteStopped)
    }

    fun stopRoulette() {
        status = RouletteStatus.STOPPING
    }

    private fun animate(
        onRouletteIndexUpdated: (newIndex: Int) -> Unit,
        onRouletteStopped: (winningIndex: Int) -> Unit
    ) {
        updateIndex()
        onRouletteIndexUpdated(optionsIndex[currentIndex])

        handler.postDelayed({
            when {
                shouldStop() -> {
                    handler.removeCallbacksAndMessages(null)
                    status = RouletteStatus.IDLE
                    mediaPlayer?.run {
                        pause()
                        seekTo(0)
                    }
                    onRouletteStopped(randomIndex)
                }
                status == RouletteStatus.STOPPING -> {
                    mediaVolume -= MEDIA_VOLUME_STEPS
                    mediaPlayer?.setVolume(mediaVolume, mediaVolume)
                    animate(onRouletteIndexUpdated, onRouletteStopped)
                }
                else -> animate(onRouletteIndexUpdated, onRouletteStopped)
            }
        }, getCurrentSpeed())
    }

    private fun updateIndex() {
        currentIndex++

        if (currentIndex >= optionsIndex.size) currentIndex = 0
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

    private fun shouldStop(): Boolean = optionsIndex[currentIndex] == randomIndex && currentSpeed == ROULETTE_SPEED_MIN

    enum class RouletteStatus {
        IDLE, PLAYING, STOPPING
    }
}
