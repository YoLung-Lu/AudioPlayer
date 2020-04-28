package com.cardinalblue.luyolung.audioplayer.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.media.session.MediaButtonReceiver
import com.cardinalblue.luyolung.audioplayer.R


const val NOW_PLAYING_CHANNEL: String = "com.cardinalblue.luyolung.audioplayer.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION: Int = 0xb333

class NotificationBuilder(private val context: Context) {
    private val platformNotificationManager: NotificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager


    fun buildNotification(mediaSession: MediaSessionCompat): NotificationCompat.Builder {

        val controller = mediaSession.controller
        val mediaMetadata = controller.metadata
        val description = mediaMetadata.description


        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL).apply {
            // Add the metadata for the currently playing track
            setContentTitle(description.title)
            setContentText(description.subtitle)
            setSubText(description.description)
            setLargeIcon(description.iconBitmap)

            // Enable launching the player by clicking the notification
            setContentIntent(controller.sessionActivity)

            // Stop the service when the notification is swiped away
            setDeleteIntent(
                MediaButtonReceiver.buildMediaButtonPendingIntent(
                    context,
                    PlaybackStateCompat.ACTION_STOP
                )
            )

            // Make the transport controls visible on the lockscreen
            setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            // Add an app icon and set its accent color
            // Be careful about the color
            setSmallIcon(R.drawable.icon_nav_arrow_left_n)
            color = ContextCompat.getColor(context, R.color.colorPrimaryDark)

            // Add a pause button
            addAction(
                NotificationCompat.Action(
                    android.R.drawable.ic_media_pause,
                    "Pause",
                    MediaButtonReceiver.buildMediaButtonPendingIntent(
                        context,
                        PlaybackStateCompat.ACTION_PLAY_PAUSE
                    )
                )
            )

            // Take advantage of MediaStyle features
//            setStyle(android.support.v4.media.app.NotificationCompat.MediaStyle()
//                .setMediaSession(mediaSession!!.sessionToken)
//                .setShowActionsInCompactView(0)
//
//                // Add a cancel button
//                .setShowCancelButton(true)
//                .setCancelButtonIntent(
//                    MediaButtonReceiver.buildMediaButtonPendingIntent(
//                        context,
//                        PlaybackStateCompat.ACTION_STOP
//                    )
//                )
//            )
        }

        return builder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nowPlayingChannelExists() =
        platformNotificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNowPlayingChannel() {
        val notificationChannel = NotificationChannel(NOW_PLAYING_CHANNEL,
            "Now Playing",
            NotificationManager.IMPORTANCE_LOW)
            .apply {
                description = "Music playing channel"
            }

        platformNotificationManager.createNotificationChannel(notificationChannel)
    }
}