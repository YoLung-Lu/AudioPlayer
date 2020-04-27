package com.cardinalblue.luyolung.audioplayer.db

import android.provider.MediaStore
import android.content.ContentResolver
import android.net.Uri
import com.cardinalblue.luyolung.audioplayer.model.Audio


class AudioRepository(private val contentResolver: ContentResolver,
                      defaultSongList: List<Uri>) {

    val audioList = mutableListOf<Audio>()

    private var currentSongIndex = 0
    private val builtInSongList = defaultSongList


    fun getListLength() = builtInSongList.size

    fun getCurrentSong() = builtInSongList[currentSongIndex]

    fun toPreviousSong(): Uri {
        if (currentSongIndex == 0) {
            currentSongIndex = getListLength() - 1
        } else {
            currentSongIndex -= 1
        }

        return getCurrentSong()
    }

    fun toNextSong(): Uri {
        currentSongIndex = (currentSongIndex + 1) % getListLength()

        return getCurrentSong()
    }

    fun loadAudio() {

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)

        if (cursor != null && cursor.getCount() > 0) {

            while (cursor.moveToNext()) {
                val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
                val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
                val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
                val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))

                // Save to audioList
                audioList.add(Audio(data, title, album, artist))
            }
        }
        cursor!!.close()
    }
}