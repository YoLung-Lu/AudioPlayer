package com.cardinalblue.luyolung.audioplayer.db

import android.provider.MediaStore
import android.content.ContentResolver
import android.content.ContentUris
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.cardinalblue.luyolung.audioplayer.model.MyAudio


class AudioRepository(private val contentResolver: ContentResolver,
                      defaultSongList: List<MyAudio> = listOf()) {

    val audioList = mutableListOf<MyAudio>()

    private var currentSongIndex = 0
    private val builtInSongList = defaultSongList

    init {
        audioList.addAll(builtInSongList)
    }

    fun getListLength() = audioList.size

    fun getCurrentSong() = audioList[currentSongIndex]

    fun toPreviousSong(): MyAudio {
        if (currentSongIndex == 0) {
            currentSongIndex = getListLength() - 1
        } else {
            currentSongIndex -= 1
        }

        return getCurrentSong()
    }

    fun toNextSong(): MyAudio {
        currentSongIndex = (currentSongIndex + 1) % getListLength()

        return getCurrentSong()
    }

    fun loadAudio() {

        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
//        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0 OR " + MediaStore.Audio.Media.IS_NOTIFICATION + "!= 0 OR " + MediaStore.Audio.Media.IS_RINGTONE + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = contentResolver.query(uri, null, selection, null, sortOrder)

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                audioFromCursor(cursor)?.let {
                    audioList.add(it)
                }
            }
        }
        cursor!!.close()

//        loadAudio2()
    }

    private fun audioFromCursor(cursor: Cursor?): MyAudio? {
        if (cursor == null) return null

        val name = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME))
        Log.d("Audio", name)

        try {
            val data = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA))
            val title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE))
            val album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM))
            val artist = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST))

            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val id = cursor.getLong(idColumn)
            val contentUri: Uri = ContentUris.withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, id)
            return MyAudio(contentUri, data, title, album, artist)
        } catch (e: Throwable) {}
        return null
    }

    fun loadAudio2() {
        // TODO: IS_PODCAST

        val uri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = contentResolver.query(uri, null, null, null, null)

        if (cursor != null && cursor.count > 0) {
            while (cursor.moveToNext()) {
                audioFromCursor(cursor)?.let {
                    audioList.add(it)
                }
            }
        }
        cursor!!.close()
    }
}