package com.cardinalblue.luyolung.audioplayer.ui

import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.media.MediaBrowserServiceCompat


private const val MY_MEDIA_ROOT_ID = "media_root_id"
private const val MY_EMPTY_MEDIA_ROOT_ID = "empty_root_id"
private const val LOG_TAG = "tag_media_service"

class MediaPlaybackService : MediaBrowserServiceCompat() {
    override fun onLoadChildren(
        parentMediaId: String,
        result: Result<List<MediaBrowserCompat.MediaItem>>
    ) {
        //  Browsing not allowed
        if (MY_EMPTY_MEDIA_ROOT_ID == parentMediaId) {
            result.sendResult(null)
            return
        }

        // Assume for example that the music catalog is already loaded/cached.

        val mediaItems = emptyList<MediaBrowserCompat.MediaItem>()

        // Check if this is the root menu:
        if (MY_MEDIA_ROOT_ID == parentMediaId) {
            // Build the MediaItem objects for the top level,
            // and put them in the mediaItems list...
        } else {
            // Examine the passed parentMediaId to see which submenu we're at,
            // and put the children of that menu in the mediaItems list...
        }
        result.sendResult(mediaItems)
    }

    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot {

        return if (allowBrowsing(clientPackageName, clientUid)) {
            // Returns a root ID that clients can use with onLoadChildren() to retrieve
            // the content hierarchy.
            BrowserRoot(MY_MEDIA_ROOT_ID, null)
        } else {
            // Clients can connect, but this BrowserRoot is an empty hierachy
            // so onLoadChildren returns nothing. This disables the ability to browse for content.
            BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
        }
    }

    private var mediaSession: MediaSessionCompat? = null
    private lateinit var stateBuilder: PlaybackStateCompat.Builder

    override fun onCreate() {
        super.onCreate()

        // Create a MediaSessionCompat
        mediaSession = MediaSessionCompat(baseContext, LOG_TAG).apply {

            // Enable callbacks from MediaButtons and TransportControls
            setFlags(MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS
                    or MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )

            // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player
            stateBuilder = PlaybackStateCompat.Builder()
                .setActions(PlaybackStateCompat.ACTION_PLAY
                        or PlaybackStateCompat.ACTION_PLAY_PAUSE
                )
            setPlaybackState(stateBuilder.build())

            // MySessionCallback() has methods that handle callbacks from a media controller
            setCallback(mySessionCallback)

            // Set the session's token so that client activities can communicate with it.
            setSessionToken(sessionToken)
        }
    }

    private val mySessionCallback = object: MediaSessionCompat.Callback() {
        override fun onPlay() {
            super.onPlay()
            startPlayer()
        }

        override fun onStop() {
            super.onStop()
            stopSelf()
        }
    }

    private fun startPlayer() {
        // Given a media session and its context (usually the component containing the session)
        // Create a NotificationCompat.Builder
        val context = applicationContext

        // Get the session's metadata
//        val controller = mediaSession!!.controller
//        val mediaMetadata = controller.metadata
//        val description = mediaMetadata.description

        val builder = NotificationBuilder(context).buildNotification(mediaSession!!)

        // Display the notification and place the service in the foreground
        startForeground(NOW_PLAYING_NOTIFICATION, builder.build())
    }


    private fun allowBrowsing(clientPackageName: String, clientUid: Int): Boolean {
        // ??
        return true
    }
}