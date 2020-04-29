package com.cardinalblue.luyolung.audioplayer.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import io.reactivex.disposables.CompositeDisposable
import android.widget.Toast
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.util.Log
import android.view.MotionEvent
import android.view.View
import com.cardinalblue.luyolung.audioplayer.R
import com.cardinalblue.luyolung.audioplayer.db.AudioRepository
import com.cardinalblue.luyolung.audioplayer.util.getNavigationBarHeight
import com.cardinalblue.luyolung.audioplayer.util.getScreenHeight
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.functions.Predicate
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.activity_audio_player.*
import java.util.concurrent.TimeUnit

// Basic audio player example from:
// https://www.tutlane.com/tutorial/android/android-audio-media-player-with-examples
// https://developer.android.com/guide/topics/media/mediaplayer


// TODO:
// 1. Separate UI and domain, stablize the model
// 2. UI: Motion layout, able to set time by bar
// 3. Model: Database, Load song and store
// 4. Model: Customize song list
// 5. Background service

class AudioPlayerActivity : AppCompatActivity() {

    private val disposableBag = CompositeDisposable()

    private var mPlayer: MediaPlayer? = null
    private var player: MediaPlayer = MediaPlayer()
    private lateinit var repository: AudioRepository

    private var oTime = 0
    private var sTime = 0
    private var eTime = 0
    private val fTime = 5000
    private val bTime = 5000
    private val hdlr = Handler()

    // Test scroller
    private var direction = -1
    private var percent = 0.8f
    private val delta = 0.05f
    private val minBound = 0.4
    private val maxBound = 0.8
    private var screenHeight = 0
    private val navigationBarHeight by lazy { getNavigationBarHeight(windowManager) }

    // Song list.
    private var currentSongIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)
        screenHeight = getScreenHeight(windowManager)

        mPlayer = MediaPlayer.create(this, R.raw.mp3_example)

        sBar.isClickable = false
        btnPause.isEnabled = false

        observeUserAction()
        observeScroller()

        repository = AudioRepository(contentResolver, getDefaultSongs())
        repository.loadAudio()
    }

    private fun observeScroller() {
        RxView.touches(scroller) { motionEvent: MotionEvent ->
            val newHeight = (motionEvent.rawY - navigationBarHeight)
            val newPercent = newHeight / screenHeight

            when (motionEvent.action) {
                MotionEvent.ACTION_MOVE -> {
                    guidelineImageBottom.setGuidelinePercent(newPercent)
                }
            }
            true
        }.subscribe().addTo(disposableBag)
    }

    private fun observeUserAction() {
        RxView.clicks(btnPlay)
            .subscribe {
                Toast.makeText(this@AudioPlayerActivity, "Playing Audio", Toast.LENGTH_SHORT).show()
                play()
            }.addTo(disposableBag)


        RxView.clicks(btnPause)
            .subscribe {
                Toast.makeText(applicationContext, "Pausing Audio", Toast.LENGTH_SHORT).show()
                mPlayer!!.pause()
                showPlayButton()

            }.addTo(disposableBag)

        RxView.clicks(btnForward)
            .subscribe {
                if (sTime + fTime <= eTime) {
                    sTime = sTime + fTime
                    mPlayer!!.seekTo(sTime)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Cannot jump forward 5 seconds",
                        Toast.LENGTH_SHORT
                    ).show()

                    mPlayer!!.reset()
                    mPlayer!!.setDataSource(applicationContext, repository.getCurrentSong())
                    mPlayer!!.prepare()
                    mPlayer!!.start()
                }
                if (!btnPlay.isEnabled) {
                    btnPlay.isEnabled = true
                }
            }.addTo(disposableBag)


        RxView.clicks(btnBackward)
            .subscribe {
                if (sTime - bTime > 0) {
                    sTime = sTime - bTime
                    mPlayer!!.seekTo(sTime)
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Cannot jump backward 5 seconds",
                        Toast.LENGTH_SHORT
                    ).show()

                    sTime = 0
                    mPlayer!!.reset()
                    mPlayer!!.setDataSource(applicationContext, repository.getCurrentSong())
                    mPlayer!!.prepare()
                    mPlayer!!.start()
                }
                if (!btnPlay.isEnabled) {
                    btnPlay.isEnabled = true
                }
            }.addTo(disposableBag)

        RxView.clicks(btnPrev)
            .subscribe {
                playSong(repository.toPreviousSong())
            }.addTo(disposableBag)

        RxView.clicks(btnNext)
            .subscribe {
                playSong(repository.toNextSong())
            }.addTo(disposableBag)
    }

    private fun play() {
        mPlayer!!.start()
        eTime = mPlayer!!.duration
        sTime = mPlayer!!.currentPosition
        if (oTime == 0) {
            sBar.max = eTime
            oTime = 1
        }
        txtSongTime!!.text = getDisplayedTimeText(eTime)
        txtStartTime!!.text = getDisplayedTimeText(sTime)

        sBar.progress = sTime
        hdlr.postDelayed(UpdateSongTime, 100)
        showPauseButton()
    }

    private val UpdateSongTime = object : Runnable {
        override fun run() {
            sTime = mPlayer!!.currentPosition
            txtStartTime!!.text = getDisplayedTimeText(sTime)

            sBar.progress = sTime
            hdlr.postDelayed(this, 100)
        }
    }

    private fun showPlayButton() {
        btnPlay.isEnabled = true
        btnPlay.isVisible = true

        btnPause.isEnabled = false
        btnPause.isVisible = false
    }

    private fun showPauseButton() {
        btnPlay.isEnabled = false
        btnPlay.isVisible = false

        btnPause.isEnabled = true
        btnPause.isVisible = true
    }

    private fun playSong(uri: Uri) {
        // UI?
        oTime = 0

        val player = mPlayer ?: return
        with(player) {
            reset()
            setDataSource(applicationContext, uri)
            prepare()
            start()
        }

        play()
    }

    private fun getDisplayedTimeText(timeMillisecond: Int): String {
        val second = timeMillisecond % 60000

        return String.format(
            "%d min, %d sec",
            TimeUnit.MILLISECONDS.toMinutes(timeMillisecond.toLong()),
            TimeUnit.MILLISECONDS.toSeconds(second.toLong())
        )
    }


    private fun getDefaultSongs(): List<Uri> {
//        resources
        val path = "android.resource://com.cardinalblue.luyolung.audioplayer/"
        val list = mutableListOf<Uri>()
        list.add(Uri.parse(path + R.raw.mp3_example))
        list.add(Uri.parse(path + R.raw.mp3_example2))
        list.add(Uri.parse(path + R.raw.mp3_example3))

//        val list = mutableListOf<String>()
//        list.add(path + R.raw.mp3_example)
//        list.add(path + R.raw.mp3_example2)
//        list.add(path + R.raw.mp3_example3)
        return list
    }
}

var View.isVisible: Boolean
    set(value) {
        visibility = if(value){
            View.VISIBLE
        }else{
            View.GONE
        }
    }
    get() = visibility == View.VISIBLE