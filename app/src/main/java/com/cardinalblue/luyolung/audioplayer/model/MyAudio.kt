package com.cardinalblue.luyolung.audioplayer.model

import android.net.Uri

data class MyAudio(
    val uri: Uri,
    val data: String,
    val title: String,
    val album: String,
    val artist: String
)
