package com.documentviewer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DocumentViewerApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(
                    CHANNEL_PDF_OPERATIONS,
                    "PDF Operations",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Notifications for PDF operations like merge, convert, etc."
                },
                NotificationChannel(
                    CHANNEL_TTS,
                    "Text to Speech",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Text to speech playback controls"
                }
            )

            val notificationManager = getSystemService(NotificationManager::class.java)
            channels.forEach { notificationManager.createNotificationChannel(it) }
        }
    }

    companion object {
        const val CHANNEL_PDF_OPERATIONS = "pdf_operations"
        const val CHANNEL_TTS = "tts_playback"
    }
}
