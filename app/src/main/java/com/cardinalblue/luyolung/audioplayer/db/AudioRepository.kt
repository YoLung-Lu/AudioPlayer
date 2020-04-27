package com.cardinalblue.luyolung.audioplayer.db

import android.provider.MediaStore
import android.content.ContentResolver
import com.cardinalblue.luyolung.audioplayer.model.Audio


class AudioRepository(private val contentResolver: ContentResolver) {

    val audioList = mutableListOf<Audio>()

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