package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.RingtoneManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.util.Log

object DriverAlertHelper {

    /**
     * Triggers distinct loud alert sound and continuous pulsing vibration
     * when a new pending booking arrives from Firebase.
     */
    fun triggerNewBookingAlert(context: Context) {
        // 1. Trigger Vibration
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            vibrator?.let {
                // Pulse pattern: 0ms wait, 400ms vibrate, 200ms pause, 400ms vibrate, 200ms pause, 600ms vibrate
                val timings = longArrayOf(0, 400, 200, 400, 200, 600)
                val amplitudes = intArrayOf(0, 255, 0, 255, 0, 255)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(timings, -1)
                }
            }
        } catch (e: Exception) {
            Log.w("DriverAlertHelper", "Vibration notification failed: ${e.message}")
        }

        // 2. Play Notification Audio / Tone
        try {
            val notificationUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val ringtone = RingtoneManager.getRingtone(context.applicationContext, notificationUri)
            if (ringtone != null) {
                ringtone.play()
            } else {
                playFallbackTone()
            }
        } catch (e: Exception) {
            playFallbackTone()
        }
    }

    private fun playFallbackTone() {
        try {
            val toneGenerator = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 100)
            toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 800)
        } catch (e: Exception) {
            Log.w("DriverAlertHelper", "Fallback tone failed: ${e.message}")
        }
    }
}
